(ns api.lib.google.optimization
  (:require ["googleapis" :refer (google)]
            ["axios" :as axios]
            ["date-fns" :as d]
            [promesa.core :as p]
            [cljs-bean.core :refer (->js ->clj)]
            [common.utils.number :refer (scale-amount)]))

(defn upscale-capacity [capacity]
  (scale-amount capacity 1000000))

(defn downscale-capacity [capacity]
  (scale-amount capacity 0.000001))

(def ^js auth-client
  (-> (-> google .-auth .-GoogleAuth)
      (new
       #js{:keyFile "resources/google/service_account.json"
           :scopes #js["https://www.googleapis.com/auth/cloud-platform"]})))

(defn transform-shipments [plan]
  (let [{:keys [startAt endAt depot shipments]} plan
        {:keys [lat lng]} depot
        depot-location {:latitude lat
                        :longitude lng}]
    (map (fn [{:keys [shipment]}]
           (let [{:keys [place windows duration]} shipment
                 {:keys [lat lng]} place
                 arrival-location {:latitude lat
                                   :longitude lng}
                 volume (upscale-capacity (:volume shipment))
                 weight (upscale-capacity (:weight shipment))]
             {;; :penalty_cost (/ volume weight)
              :load_demands
              {:volume {:amount volume}
               :weight {:amount weight}}
              :pickups
              [{:arrival_location depot-location
                :time_windows [{:start_time (-> startAt .toISOString)
                                :end_time (-> endAt .toISOString)}]
                :duration (str "120" "s")}]
              :deliveries
              [{:arrival_location arrival-location
                :time_windows (map
                               (fn [window]
                                 (let [start (d/max (array startAt (-> window :start js/Date.)))
                                       end (d/min (array endAt (-> window :end js/Date.)))]
                                   {:start_time (-> start .toISOString)
                                    :end_time (-> end .toISOString)}))
                               windows)
                :duration (str duration "s")}]}))
         shipments)))

(defn transform-vehicles [plan]
  (let [{:keys [breaks depot vehicles]} plan
        {:keys [lat lng]} depot
        depot-location {:latitude lat
                        :longitude lng}]
    (map (fn [{:keys [vehicle]}]
           (let [volume (upscale-capacity (:volume vehicle))
                 weight (upscale-capacity (:weight vehicle))
                 base-payload {:start_location depot-location
                               :end_location depot-location
                               :cost_per_kilometer (/ volume weight)
                               :load_limits {:volume {:max_load volume}
                                             :weight {:max_load weight}}}
                 breaks-payload {:break_rule
                                 {:break_requests
                                  (map
                                   (fn [break]
                                     (let [start (-> break :start js/Date.)
                                           end (-> break :end js/Date.)
                                           duration (d/differenceInSeconds end start)]
                                       {:earliest_start_time (-> start .toISOString)
                                        :latest_start_time (-> (d/addSeconds end duration) .toISOString)
                                        :min_duration (str duration "s")}))
                                   breaks)}}]
             (if (seq breaks)
               (merge base-payload breaks-payload)
               base-payload)))
         vehicles)))

(defn transform-plan [plan]
  (let [{:keys [startAt endAt result]} plan
        {:keys [routes]} result]
    {:injected_first_solution_routes routes
     :populate_polylines true
     :model
     {:global_start_time (-> startAt .toISOString)
      :global_end_time (-> endAt .toISOString)
      :shipments (transform-shipments plan)
      :vehicles (transform-vehicles plan)}}))

(defn optimize-plan [plan]
  (p/let [payload (transform-plan (->clj plan))
          url "https://cloudoptimization.googleapis.com/v1/projects/dispatch-368818:optimizeTours"
          token (.getAccessToken auth-client)
          options (->js {:headers
                         {:Content-Type "application/json"
                          :Authorization (str "Bearer " token)}})]
    (.post axios url (->js payload) options)))

(defn merge-pickups [visits]
  (let [merged-visits (reduce
                       (fn [acc obj]
                         (if (and (contains? obj :isPickup)
                                  (= true (get obj :isPickup))
                                  (= true (contains? (last acc) :isPickup)))
                           (conj (pop acc) (last acc))
                           (conj acc obj)))
                       [{}] visits)]
    (rest merged-visits)))

(defn parse-result [^js plan]
  (let [^js result (.. plan -result)
        ^js routes (some-> result .-routes)
        ^js shipments (.. plan -shipments)
        ^js vehicles (.. plan -vehicles)]
    (when routes
      #js{:routes (apply
                   array
                   (map-indexed
                    (fn [idx ^js route]
                      #js{:vehicle (.-vehicle (get vehicles idx))
                          :start (some-> route .-vehicleStartTime)
                          :end (some-> route .-vehicleEndTime)
                          :meters (some-> route .-metrics .-travelDistanceMeters)
                          :volume (some-> route .-metrics .-maxLoads .-volume .-amount downscale-capacity)
                          :weight (some-> route .-metrics .-maxLoads .-weight .-amount downscale-capacity)
                          :visits (apply array
                                         (map
                                          (fn [^js visit]
                                            (let [pickup? (.-isPickup visit)]
                                              #js{:arrival (.. visit -startTime)
                                                  :depot (when pickup? (.-depot plan))
                                                  :shipment (when-not pickup? (.-shipment (get shipments (or (.-shipmentIndex visit) 0))))}))
                                          (-> route .-visits ->clj merge-pickups ->js)))})
                    routes))
          :skipped (apply array
                          (map
                           (fn [^js shipment]
                             (.-shipment (get shipments (or (.-index shipment) 0))))
                           (.-skippedShipments result)))})))
