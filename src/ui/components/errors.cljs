(ns ui.components.errors
  (:require
   [ui.components.callout :refer (callout)]
   [ui.utils.error :refer (tr-error)]))

(defn error [error]
  (when error
    [callout "error" error]))

(defn errors [anoms]
  [:<>
   (doall (for [anom anoms]
            ^{:key (:reason anom)}
            [error (tr-error anom)]))])
