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
 :location/update
 [trim-v]
 (fn [db [val]]
   (assoc
    db :location val)))
