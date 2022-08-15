(ns app.subs
  (:require [re-frame.core :as rf]))

(defn listen [query-vector]
  @(rf/subscribe query-vector))

(rf/reg-sub
 :locale
 #(:locale %))

(rf/reg-sub
 :locale/tempura-config
 #(-> %
      :locale
      :language
      keyword
      vector))

(rf/reg-sub
 :locale/language
 #(-> % :locale :language))

(rf/reg-sub
 :locale/region
 #(-> % :locale :region))

(rf/reg-sub
 :origin
 #(:origin %))

(rf/reg-sub
 :location
 #(:location %))

(rf/reg-sub
 :stops
 #(:stops %))

(rf/reg-sub
 :route
 #(:route %))

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
