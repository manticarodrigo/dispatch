(ns app.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :location/current
 (fn [db _]
   (:location db)))
