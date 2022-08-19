(ns app.views.route.utils
  (:require [app.utils.i18n :refer (tr)]
            [app.utils.string :refer (class-names)]))


(defonce distance-str #(tr [:views.route/distance]))
(defonce duration-str #(tr [:views.route/duration]))

(defonce padding-x "px-3 sm:px-4 md:px-6 lg:px-8")
(defonce padding-y "py-3 sm:px-4 md:py-6")
(defonce padding (class-names padding-x padding-y))
