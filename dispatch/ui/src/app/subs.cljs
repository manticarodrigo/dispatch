(ns app.subs
  (:require
   [re-frame.core :as rf]))

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
