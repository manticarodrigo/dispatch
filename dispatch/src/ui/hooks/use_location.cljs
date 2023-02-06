(ns ui.hooks.use-location
  (:require
   [react :refer (useEffect)]
   [promesa.core :as p]
   [ui.lib.location :refer (watch-position)]))

(def !cleanup (atom nil))

(defn use-location []
  (useEffect
   (fn []
     (fn []
       (when @!cleanup
         (@!cleanup)
         (reset! !cleanup nil))))
   #js[])
  (fn [cb]
    (p/let [cleanup (watch-position cb)]
      (reset! !cleanup cleanup))))
