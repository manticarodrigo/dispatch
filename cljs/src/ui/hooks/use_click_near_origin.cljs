(ns ui.hooks.use-click-near-origin
  (:require ["react" :refer (useRef)]))

(defn use-click-near-origin []
  (let [pointer (useRef {:x 0 :y 0})]
    {:get-on-click (fn [cb]
                     #(let [{:keys [x y]} (.-current pointer)]
                        (when (and (<= (.-clientX %) (+ x 10))
                                   (>= (.-clientX %) (- x 10))
                                   (<= (.-clientY %) (+ y 10))
                                   (>= (.-clientY %) (- y 10)))
                          (cb %))))
     :on-mouse-down #(set! (.-current pointer) {:x (.-clientX %) :y (.-clientY %)})}))
