(ns tests.fixtures.tour
  (:require ["csv-parse/sync" :refer (parse)]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]))

(defn military-time-to-minutes [military-time]
  (let [time (if (= (count military-time) 3) (str "0" military-time) military-time)
        hours (-> time
                  (subs 0 2)
                  (js/parseInt))
        minutes (-> time
                    (subs 2)
                    (js/parseInt))]
    (+ (* hours 60) minutes)))

(defn get-date [military-time]
  (-> (js/Date.)
      (d/startOfDay)
      (d/addDays 1)
      (d/addMinutes (military-time-to-minutes military-time))))

(defn scale-amount [amount factor]
  (-> amount js/parseFloat (* factor) js/parseInt))

(defn get-window [start end]
  (when (and (not-empty start) (not-empty end))
    {:start (-> start get-date .toISOString)
     :end (-> end get-date .toISOString)}))

(def shipments
  (->> (parse
        (inline "fixtures/optimization/shipments.csv")
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
        (inline "fixtures/optimization/vehicles.csv")
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
  (->>
   (parse
    (inline "fixtures/optimization/depot.csv")
    (->js {:columns
           ["reference"
            "description"
            "address"
            "startTime"
            "endTime"
            "waitDuration"
            "latitude"
            "longitude"]
           :from 2}))
   ->clj
   (map
    (fn [{:keys [description address startTime endTime latitude longitude]}]
      {:place {:name description
               :description address
               :lat (js/parseFloat latitude)
               :lng (js/parseFloat longitude)}
       :startAt (-> startTime get-date .toISOString)
       :endAt (-> endTime get-date .toISOString)
       :breaks [(get-window "1200" "1230")]}))
   first))

(def solution (js/JSON.parse (inline "fixtures/optimization/solution.json")))
