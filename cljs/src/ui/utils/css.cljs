(ns ui.utils.css
  (:require [ui.utils.string :refer (class-names)]))

(defonce padding-x "px-3 sm:px-4 md:px-4 lg:px-6")
(defonce padding-y "py-3 md:py-4")
(defonce padding (class-names padding-x padding-y))
