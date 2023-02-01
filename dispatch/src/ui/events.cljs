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
 (fn [] db/default-db))

(rf/reg-event-db
 :session/set
 [trim-v]
 (assoc-key :session))

(rf/reg-event-db
 :language/set
 [trim-v]
 (assoc-key :language))

(rf/reg-event-db
 :device/set
 [trim-v]
 (assoc-key :device))

(rf/reg-event-db
 :map/set-paths
 [trim-v]
 (fn [db [v]]
   (assoc-in
    db [:map :paths] v)))

(rf/reg-event-db
 :map/set-points
 [trim-v]
 (fn [db [v]]
   (assoc-in
    db [:map :points] v)))
