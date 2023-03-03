(ns ui.lib.floating
  (:require ["@floating-ui/react-dom" :refer (useFloating offset flip shift)]
            [cljs-bean.core :refer (->clj)]))

(defn use-floating []
  (->clj
   (useFloating
    #js{:placement "bottom"
        :middleware #js[(offset 10)
                        (shift #js{:padding 10})
                        (flip)]})))
