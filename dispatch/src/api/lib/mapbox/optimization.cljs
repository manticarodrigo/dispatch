(ns api.lib.mapbox.optimization
  (:require ["axios" :as axios]
            ["date-fns" :as d]
            [promesa.core :as p]
            [cljs-bean.core :refer (->js)]))

(def token "pk.eyJ1IjoibWFudGljYXJvZHJpZ28iLCJhIjoiY2xlcWUwNG16MGw0ejNza216ZjM5bWljaiJ9.N_TZRydBKQqVSR-uq6q40w")

(defn optimize-tours [payload]
  (p/let [url (str "https://api.mapbox.com/optimized-trips/v2?access_token=" token)
          options (->js {:headers {:Content-Type "application/json"}})]
    (.post axios url (->js payload) options)))

(defn transform-locations [shipments]
  (map-indexed
   (fn [idx {:keys [place]}]
     {:name (str idx) :coordinates [(:lng place) (:lat place)]})
   shipments))

(defn transform-shipments [shipments]
  (map-indexed
   (fn [idx {:keys [id size windows duration]}]
     {:name id
      :from "depot"
      :to (str idx)
      :size size
      :dropoff_duration duration
      :pickup_times (map
                     (fn [{:keys [start end]}]
                       {:earliest start
                        :lastest end
                        :type "soft_end"})
                     windows)})
   shipments))

(defn transform-vehicles [depot vehicles]
  (let [{:keys [startAt endAt breaks]} depot]
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

(defn transform-tour [depot shipments vehicles]
  (let [{:keys [latitude longitude]} depot]
    {:version 1
     :locations (concat
                 [{:name "depot" :coordinates [longitude latitude]}]
                 (transform-locations shipments))
     :shipments (transform-shipments shipments)
     :vehicles (transform-vehicles depot vehicles)}))
