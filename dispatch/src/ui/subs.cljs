(ns ui.subs
  (:require [re-frame.core :as rf]))

(defn listen [query-vector]
  @(rf/subscribe query-vector))


(rf/reg-sub :session #(:session %))
(rf/reg-sub :language #(-> (or (some-> % :language)
                               (some-> % :device :language)
                               (condp = %
                                 "en" "en"
                                 "es" "es"
                                 "en"))))
(rf/reg-sub :device #(some-> % :device))
(rf/reg-sub :map #(some-> % :map))

(rf/reg-sub :layout/nav-open #(some-> % :layout :nav-open))
(rf/reg-sub :layout/sidebar-open #(some-> % :layout :sidebar-open))
