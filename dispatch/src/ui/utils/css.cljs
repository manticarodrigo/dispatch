(ns ui.utils.css
  (:require [ui.utils.string :refer (class-names)]))

(defonce padding-x "px-3 sm:px-4 md:px-6 lg:px-8")
(defonce padding-y "py-3 sm:py-4 md:py-6")
(defonce padding (class-names padding-x padding-y))
