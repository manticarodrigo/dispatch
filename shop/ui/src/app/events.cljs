(ns app.events
  (:require
   [re-frame.core :as rf]
   [app.db :as db]))


(rf/reg-event-db
 ::initialize-db
 (fn []
   db/default-db))

(rf/reg-event-db
 :location/update
 (fn [db [_ val]]
   (assoc
    db :location val)))
