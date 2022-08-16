(ns app.views.route.utils
  (:require [app.utils.i18n :refer (tr)]))


(defonce distance-str #(tr [:route-view.common/distance]))
(defonce duration-str #(tr [:route-view.common/duration]))

(defonce padding-x "px-2 sm:px-4 md:px-6 lg:px-8")
(defonce padding "p-2 sm:p-4 md:p-6 lg:p-8")
