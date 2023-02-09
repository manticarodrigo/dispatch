(ns ui.subs
  (:require [re-frame.core :as rf]))

(defn listen [query-vector]
  @(rf/subscribe query-vector))


(rf/reg-sub :session #(:session %))
(rf/reg-sub :language #(or (some-> % :language)
                           (some-> % :device :language)))
(rf/reg-sub :device #(some-> % :device))
(rf/reg-sub :map/paths #(some-> % :map :paths))
(rf/reg-sub :map/points #(some-> % :map :points))
(rf/reg-sub :map/locations #(some-> % :map :locations))
