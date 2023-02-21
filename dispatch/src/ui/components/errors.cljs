(ns ui.components.errors
  (:require [ui.utils.error :refer (tr-error)]))

(defn error [message]
  (when message
    [:span {:class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
     message]))

(defn errors [anoms]
  [:<>
   (doall (for [anom anoms]
            ^{:key (:reason anom)}
            [error (tr-error anom)]))])
