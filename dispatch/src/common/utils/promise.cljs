(ns common.utils.promise
  (:require [cljs-bean.core :refer (->js)]))

(defn each [fns]
  (js/Promise.
   (fn [resolve reject]
     (let [promise-fns (->js fns)
           results #js[]
           initial-promise (.shift promise-fns)
           handle-result (fn handle-result [result]
                           (.push results result)
                           (if (> (.-length promise-fns) 0)
                             (let [next-promise (.shift promise-fns)]
                               (->
                                (next-promise)
                                (.then handle-result)
                                (.catch reject)))
                             (resolve results)))]
       (->
        (initial-promise)
        (.then handle-result)
        (.catch reject))))))
