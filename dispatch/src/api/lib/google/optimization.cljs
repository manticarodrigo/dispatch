(ns api.lib.google.optimization
  (:require ["googleapis" :refer (google)]
            ["axios" :as axios]
            ["date-fns" :as d]
            [promesa.core :as p]
            [cljs-bean.core :refer (->js)]))

(def ^js auth-client
  (-> (-> google .-auth .-GoogleAuth)
      (new
       #js{:keyFile "resources/google/service_account.json"
           :scopes #js["https://www.googleapis.com/auth/cloud-platform"]})))

(defn optimize-tours [payload]
  (p/let [url "https://cloudoptimization.googleapis.com/v1/projects/dispatch-368818:optimizeTours"
          token (.getAccessToken auth-client)
          options (->js {:headers
                         {:Content-Type "application/json"
                          :Authorization (str "Bearer " token)}})]
    (.post axios url payload options)))

(defn transform-shipments [shipments]
  (map (fn [{:keys [size windows duration latitude longitude]}]
         {:deliveries
          [{:arrival_location {:latitude latitude
                               :longitude longitude}
            :time_windows (map
                           (fn [{:keys [start end]}]
                             {:start_time start
                              :end_time end})
                           windows)
            :duration (str duration "s")}]
          :load_demands {:volume {:amount (:volume size)}
                         :weight {:amount (:weight size)}}})
       shipments))

(defn transform-vehicles [depot vehicles]
  (let [{:keys [startAt endAt latitude longitude breaks]} depot
        depot-location {:latitude latitude
                        :longitude longitude}]
    (map (fn [{:keys [capacities]}]
           {:start_location depot-location
            :end_location depot-location
            :start_time_windows [{:start_time (-> startAt .toISOString)}]
            :end_time_windows [{:end_time (-> endAt .toISOString)}]
            :load_limits {:volume {:max_load (:volume capacities)}
                          :weight {:max_load (:weight capacities)}}
            :break_rule {:break_requests
                         (map
                          (fn [{:keys [start end]}]
                            {:earliest_start_time (-> start .toISOString)
                             :latest_start_time (-> end .toISOString)
                             :min_duration (str (d/differenceInSeconds end start) "s")})
                          breaks)}})
         vehicles)))

(defn transform-tour [depot shipments vehicles]
  (let [{:keys [startAt endAt]} depot]
    {:model
     {:global_start_time (-> startAt .toISOString)
      :global_end_time (-> endAt .toISOString)
      :shipments (transform-shipments shipments)
      :vehicles (transform-vehicles depot vehicles)}}))

(defn parse-result [^js plan]
  (let [^js result (.. plan -result)
        ^js routes (.. result -routes)
        ^js shipments (.. plan -shipments)
        ^js vehicles (.. plan -vehicles)]
    (apply array
           (map-indexed
            (fn [idx ^js route]
              #js{:vehicle (get vehicles idx)
                  :start (.. route -vehicleStartTime)
                  :end (.. route -vehicleEndTime)
                  :meters (.. route -metrics -travelDistanceMeters)
                  :volume (.. route -metrics -maxLoads -volume -amount)
                  :weight (.. route -metrics -maxLoads -weight -amount)
                  :shipments (apply array
                                    (map
                                     (fn [^js visit]
                                       (get shipments (or 0 (.-shipmentIndex visit))))
                                     (.-visits route)))})
            routes))))
