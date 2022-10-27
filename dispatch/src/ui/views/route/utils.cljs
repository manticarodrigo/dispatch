(ns ui.views.route.utils
  (:require [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]))


(defonce distance-str #(tr [:view.route/distance]))
(defonce duration-str #(tr [:view.route/duration]))

(defonce padding-x "px-3 sm:px-4 md:px-6 lg:px-8")
(defonce padding-y "py-3 sm:px-4 md:py-6")
(defonce padding (class-names padding-x padding-y))
