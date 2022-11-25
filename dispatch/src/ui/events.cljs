(ns ui.events
  (:require
   [re-frame.core :as rf :refer [trim-v]]
   [ui.db :as db]))

(defn assoc-key [key]
  (fn [db [v]]
    (assoc
     db key v)))

(rf/reg-event-db
 ::initialize-db
 (fn [] db/seed-db))

(rf/reg-event-db
 :session/set
 [trim-v]
 (assoc-key :session))

(rf/reg-event-db
 :locale/set
 [trim-v]
 (assoc-key :locale))

(rf/reg-event-db
 :search/set
 [trim-v]
 (assoc-key :search))

(rf/reg-event-db
 :origin/set
 [trim-v]
 (assoc-key :origin))

(rf/reg-event-db
 :location/set
 [trim-v]
 (assoc-key :location))

(rf/reg-event-db
 :route/set
 [trim-v]
 (assoc-key :route))
