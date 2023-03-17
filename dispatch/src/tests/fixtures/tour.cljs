(ns tests.fixtures.tour
  (:require ["papaparse" :refer (parse)]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj)]
            [common.utils.number :refer (scale-amount)]
            [common.utils.date :refer (military->minutes)]))

(defn get-date [military-time]
  (-> (js/Date.)
      (d/startOfDay)
      (d/addDays 1)
      (d/addMinutes (military->minutes military-time))))

(defn get-window [start end]
  (when (and (not-empty start) (not-empty end))
    {:start (-> start get-date .toISOString)
     :end (-> end get-date .toISOString)}))

(def shipments
  (->> (parse
        (inline "fixtures/optimization/shipments.csv")
        #js{:header true
            :transformHeader (fn [name idx]
                               (case idx
                                 0 "order"
                                 1 "reference"
                                 2 "address"
                                 3 "start1"
                                 4 "end1"
                                 5 "start2"
                                 6 "end2"
                                 7 "duration"
                                 8 "volume"
                                 9 "weight"
                                 10 "latitude"
                                 11 "longitude"
                                 name))})
       ->clj
       :data
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
           :weight (js/parseFloat weight)
           :volume (js/parseFloat volume)
           :duration (scale-amount duration 60)
           :windows (filter some? [(get-window start1 end1)
                                   (get-window start2 end2)])
           :latitude (js/parseFloat latitude)
           :longitude (js/parseFloat longitude)}))))

(def vehicles
  (->> (parse
        (inline "fixtures/optimization/vehicles.csv")
        #js{:header true
            :transformHeader (fn [name idx]
                               (case idx
                                 0 "description"
                                 1 "maxWeight"
                                 2 "maxVolume"
                                 3 "maxItems"
                                 4 "maxDuration"
                                 5 "loadingTime"
                                 6 "avgSpeed"
                                 7 "breakStart"
                                 8 "breakEnd"
                                 9 "breakDuration"
                                 10 "minBreakDuration"
                                 name))})
       ->clj
       :data
       (map
        (fn [{:keys [description maxWeight maxVolume]}]
          {:name description
           :weight (js/parseFloat maxWeight)
           :volume (js/parseFloat maxVolume)}))))

(def depot
  (->>
   (parse
    (inline "fixtures/optimization/depot.csv")
    #js{:header true
        :transformHeader (fn [name idx]
                           (case idx
                             0 "reference"
                             1 "description"
                             2 "address"
                             3 "startTime"
                             4 "endTime"
                             5 "waitDuration"
                             6 "latitude"
                             7 "longitude"
                             name))})
   ->clj
   :data
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
