(ns app.events
  (:require
   [re-frame.core :as rf :refer [trim-v]]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [app.db :as db]))


(rf/reg-event-db
 ::initialize-db
 (fn-traced []
            db/default-db))

(rf/reg-event-db
 :location/set
 [trim-v]
 (fn [db [v]]
   (assoc
    db :location v)))

(rf/reg-event-db
 :route/set
 [trim-v]
 (fn [db [v]]
   (assoc
    db :route v)))
