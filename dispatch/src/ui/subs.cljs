(ns ui.subs
  (:require [re-frame.core :as rf]))

(defn listen [query-vector]
  @(rf/subscribe query-vector))


(rf/reg-sub :session #(:session %))
(rf/reg-sub :locale/language #(some-> % :locale :language))
(rf/reg-sub :locale/region #(some-> % :locale :region))
(rf/reg-sub :map/paths #(some-> % :map :paths))
(rf/reg-sub :map/points #(some-> % :map :points))
