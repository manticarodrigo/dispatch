(ns api.lib.mapbox.optimization
  (:require ["axios" :as axios]
            ["date-fns" :as d]
            [cljs-bean.core :refer (->js ->clj)]))

(def token "pk.eyJ1IjoibWFudGljYXJvZHJpZ28iLCJhIjoiY2xlcWUwNG16MGw0ejNza216ZjM5bWljaiJ9.N_TZRydBKQqVSR-uq6q40w")

(defn transform-locations [shipments]
  (map
   (fn [{:keys [place]}]
     {:name (:id place) :coordinates [(:lng place) (:lat place)]})
   shipments))

(defn transform-shipments [shipments]
  (map
   (fn [{:keys [id size windows duration place]}]
     {:name id
      :from "depot"
      :to (:id place)
      :size size
      :dropoff_duration duration
      :dropoff_times (map
                      (fn [{:keys [start end]}]
                        {:earliest start
                         :latest end
                         :type "soft_end"})
                      windows)})
   shipments))

(defn transform-vehicles [plan]
  (let [{:keys [startAt endAt breaks vehicles]} plan]
    (map
     (fn [{:keys [id capacities]}]
       {:name id
        :start_location "depot"
        :end_location "depot"
        :capacities capacities
        :earliest_start startAt
        :latest_end endAt
        :breaks (map
                 (fn [{:keys [start end]}]
                   {:earliest_start start
                    :latest_end end
                    :duration (d/differenceInSeconds (js/Date. end) (js/Date. start))})
                 breaks)})
     vehicles)))

(defn transform-plan [plan]
  (let [{:keys [depot shipments]} plan
        {:keys [lat lng]} depot]
    {:version 1
     :locations (concat
                 [{:name "depot" :coordinates [lng lat]}]
                 (transform-locations (take 2 shipments)))
     :shipments (transform-shipments (take 2 shipments))
     :vehicles (transform-vehicles plan)}))

(defn optimize-plan [plan]
  (let [payload (transform-plan (->clj plan))
        url (str "https://api.mapbox.com/optimized-trips/v2?access_token=" token)
        options (->js {:headers {:Content-Type "application/json"}})]
    (.post axios url (->js payload) options)))

(defn fetch-plans []
  (let [url (str "https://api.mapbox.com/optimized-trips/v2?access_token=" token)]
    (.get axios url)))

(defn fetch-plan [id]
  (let [url (str "https://api.mapbox.com/optimized-trips/v2/" id "?access_token=" token)]
    (.get axios url)))
