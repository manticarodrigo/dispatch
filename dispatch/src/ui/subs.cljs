(ns ui.subs
  (:require [re-frame.core :as rf]))

(defn listen [query-vector]
  @(rf/subscribe query-vector))


(rf/reg-sub :session #(:session %))
(rf/reg-sub :language #(some-> % :language))
(rf/reg-sub :map/paths #(some-> % :map :paths))
(rf/reg-sub :map/points #(some-> % :map :points))
