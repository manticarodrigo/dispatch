(ns ui.lib.floating
  (:require ["@floating-ui/react-dom" :refer (useFloating offset flip shift)]
            [cljs-bean.core :refer (->clj ->js)]))

(def base {:placement "bottom"
           :middleware #js[(offset 10)
                           (shift #js{:padding 10})
                           (flip)]})

(defn use-floating
  ([]
   (->clj
    (useFloating
     (->js base))))
  ([opts]
   (->clj
    (useFloating
     (->js
      (merge
       base
       opts))))))
