(ns ui.components.errors
  (:require [ui.utils.error :refer (tr-error)]))

(defn errors [anoms]
  [:<>
   (doall (for [anom anoms]
            [:span {:key (:reason anom)
                    :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
             (tr-error anom)]))])
