(ns tests.fixtures.tour
  (:require ["csv-parse/sync" :refer (parse)]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]))

(defn get-date [military-time]
  (-> (js/Date.)
      (d/startOfDay)
      (d/addHours (-> military-time js/parseInt (/ 100)))))

(defn scale-amount [amount factor]
  (-> amount js/parseFloat (* factor) js/parseInt))

(defn get-window [start end]
  (when (and (not-empty start) (not-empty end))
    {:start (-> start get-date .toISOString)
     :end (-> end get-date .toISOString)}))

(def shipments
  (->> (parse
        (inline "samples/shipments.csv")
        (->js {:columns
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
       ->clj
       (map
        (fn [{:keys [reference
                     address
                     start1
                     end1
                     start2
                     end2
                     duration
                     volume
                     weight
                     latitude
                     longitude]}]
          {:reference reference
           :address address
           :size {:volume (scale-amount volume 100000)
                  :weight (scale-amount weight 1000)}
           :duration (scale-amount duration 60)
           :windows (filter some? [(get-window start1 end1)
                                   (get-window start2 end2)])
           :latitude (js/parseFloat latitude)
           :longitude (js/parseFloat longitude)}))))

(def vehicles
  (->> (parse
        (inline "samples/vehicles.csv")
        (->js {:columns
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
       ->clj
       (map
        (fn [{:keys [description maxWeight maxVolume]}]
          {:name description
           :capacities {:volume (scale-amount maxVolume 100000)
                        :weight (scale-amount maxWeight 1000)}}))))

(def depot
  (-> (inline "samples/depot.csv")
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
