(ns tests.fixtures.tour
  (:require ["papaparse" :refer (parse)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj)]
            [common.utils.date :refer (military->date military->window)]))

(def places
  (->> (parse
        (inline "fixtures/optimization/places.csv")
        #js{:header true
            :transformHeader (fn [name idx]
                               (case idx
                                 0 "externalId"
                                 1 "name"
                                 2 "description"
                                 3 "latitude"
                                 4 "longitude"
                                 name))})
       ->clj
       :data
       (map
        (fn [{:keys [externalId
                     name
                     description
                     latitude
                     longitude]}]
          {:externalId externalId
           :name name
           :description description
           :lat (js/parseFloat latitude)
           :lng (js/parseFloat longitude)}))))

(def shipments
  (->> (parse
        (inline "fixtures/optimization/shipments.csv")
        #js{:header true
            :transformHeader (fn [name idx]
                               (case idx
                                 0 "externalId"
                                 1 "externalPlaceId"
                                 2 "weight"
                                 3 "volume"
                                 4 "duration"
                                 5 "start1"
                                 6 "end1"
                                 7 "start2"
                                 8 "end2"
                                 name))})
       ->clj
       :data
       (map
        (fn [{:keys [externalId
                     externalPlaceId
                     weight
                     volume
                     duration
                     start1
                     end1
                     start2
                     end2]}]
          {:externalId externalId
           :externalPlaceId externalPlaceId
           :weight (js/parseFloat weight)
           :volume (js/parseFloat volume)
           :duration (js/parseInt duration)
           :windows (filter some? [(military->window start1 end1)
                                   (military->window start2 end2)])}))))

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
       :startAt (-> startTime military->date .toISOString)
       :endAt (-> endTime military->date .toISOString)
       :breaks [(military->window "1200" "1230")]}))
   first))

(def solution (js/JSON.parse (inline "fixtures/optimization/solution.json")))
