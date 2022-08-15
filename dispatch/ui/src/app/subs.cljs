(ns app.subs
  (:require [re-frame.core :as rf]))

(defn listen [query-vector]
  @(rf/subscribe query-vector))

(rf/reg-sub
 :location/current
 (fn [db]
   (:location db)))

(rf/reg-sub
 :stops/current
 (fn [db]
   (:stops db)))

(rf/reg-sub
 :route/current
 (fn [db]
   (:route db)))

(rf/reg-sub
 :route/minutes
 (fn [db]
   (let [route (:route db)
         durations (mapv #(-> % :duration :value) route)
         seconds (reduce + durations)]
     (js/Math.round (/ seconds 60)))))

(rf/reg-sub
 :route/kilometers
 (fn [db]
   (let [route (:route db)
         distances (mapv #(-> % :distance :value) route)
         meters (reduce + distances)]
     (js/Math.round (/ meters 1000)))))
