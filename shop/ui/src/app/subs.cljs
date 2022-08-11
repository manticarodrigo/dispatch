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
 :ref-location/current
 (fn [db]
   (:ref-location db)))
