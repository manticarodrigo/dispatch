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
 :session
 [trim-v]
 (assoc-key :session))

(rf/reg-event-db
 :language
 [trim-v]
 (assoc-key :language))

(rf/reg-event-db
 :device
 [trim-v]
 (assoc-key :device))

(rf/reg-event-db
 :device/position
 [trim-v]
 (fn [db [v]]
   (assoc-in
    db [:device :position] v)))

(rf/reg-event-db
 :map
 [trim-v]
 (assoc-key :map))

(rf/reg-event-db
 :layout/toggle-nav
 (fn [db]
   (assoc-in
    db [:layout :nav-open] (not (get-in db [:layout :nav-open])))))

(rf/reg-event-db
 :layout/toggle-sidebar
 (fn [db]
   (assoc-in
    db [:layout :sidebar-open] (not (get-in db [:layout :sidebar-open])))))
