(ns api.lib.google.optimization
  (:require ["googleapis" :refer (google)]
            ["axios" :as axios]
            ["csv-parse/sync" :refer (parse)]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [cljs-bean.core :refer (->clj ->js)]))

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
    (-> (.post axios url payload options)
        (.then (fn [response]
                 (-> response .-data))))))

(def orders
  (-> (inline "samples/orders.csv")
      (parse (->js {:columns
                    ["order"
                     "reference"
                     "address"
                     "start1"
                     "end1"
                     "start2"
                     "end2"
                     "duration"
                     "volume"
                     "weight"
                     "latitude"
                     "longitude"]
                    :from 2}))
      ->clj))

(def vehicles
  (-> (inline "samples/vehicles.csv")
      (parse (->js {:columns
                    ["description"
                     "maxWeight"
                     "maxVolume"
                     "maxItems"
                     "maxDuration"
                     "loadingTime"
                     "avgSpeed"
                     "breakStart"
                     "breakEnd"
                     "breakDuration"
                     "minBreakDuration"]
                    :from 2}))
      ->clj))

(def warehouse
  (-> (inline "samples/warehouse.csv")
      (parse (->js {:columns
                    ["reference"
                     "description"
                     "address"
                     "startTime"
                     "endTime"
                     "waitDuration"
                     "latitude"
                     "longitude"]
                    :from 2}))
      first
      ->clj))

(defn get-date [military-time]
  (-> (js/Date.)
      (d/startOfDay)
      (d/addHours (-> military-time js/parseInt (/ 100)))))

(def global_start_date (-> warehouse :startTime get-date))
(def global_end_date (-> warehouse :endTime get-date))

(def global_return_location
  {:latitude (-> warehouse :latitude js/parseFloat)
   :longitude (-> warehouse :longitude js/parseFloat)})

(defn scale-load [amount factor]
  (-> amount js/parseFloat (* factor) js/parseInt))

(defn scale-duration [minutes]
  (str (-> minutes js/parseFloat (* 60)) "s"))

(defn get-window [start end]
  (when (and (not-empty start) (not-empty end))
    {:start_time (-> (d/max #js[(get-date start) global_start_date]) d/formatRFC3339)
     :end_time (-> (d/min #js[(get-date end) global_end_date]) d/formatRFC3339)}))

(def tour
  (->js
   {:model
    {:global_start_time (-> global_start_date d/formatRFC3339)
     :global_end_time (-> global_end_date d/formatRFC3339)
     :shipments
     (map
      (fn [{:keys [start1 end1 start2 end2 duration volume weight latitude longitude]}]
        {:deliveries
         [{:arrival_location {:latitude (js/parseFloat latitude)
                              :longitude (js/parseFloat longitude)}
           :time_windows (filter some?
                                 [(get-window start1 end1)
                                  (get-window start2 end2)])
           :duration (scale-duration duration)}]
         :load_demands {:volume {:amount (scale-load volume 100000)}
                        :weight {:amount (scale-load weight 1000)}}})
      orders)
     :vehicles
     (map
      (fn [{:keys [maxWeight maxVolume breakStart minBreakDuration]}]
        {:start_location global_return_location
         :end_location global_return_location
         :start_time_windows [{:start_time (-> global_start_date d/formatRFC3339)}]
         :end_time_windows [{:end_time (-> global_end_date d/formatRFC3339)}]
         :load_limits {:volume {:max_load (scale-load maxVolume 100000)}
                       :weight {:max_load (scale-load maxWeight 1000)}}
         :break_rule {:break_requests
                      [{:earliest_start_time (-> breakStart get-date d/formatRFC3339)
                        :latest_start_time (-> breakStart get-date d/formatRFC3339)
                        :min_duration (scale-duration minBreakDuration)}]}})
      vehicles)}}))

;; (js/console.log (js/JSON.stringify tour))
;; (-> (optimize-tours tour)
;;     (.then #(js/console.log (js/JSON.stringify %)))
;;     (.catch #(prn (.. ^js % -response -data -error -message))))
