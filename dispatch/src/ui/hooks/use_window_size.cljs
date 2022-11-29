(ns ui.hooks.use-window-size
  (:require [react :refer (useState useEffect)]))

(defn get-window-size []
  {:width (.-innerWidth js/window)
   :height (.-innerHeight js/window)})

(defn use-window-size []
  (let [[size set-size] (useState (get-window-size))]
    (prn size)
    (useEffect
     (fn []
       (let [listener #(set-size (get-window-size))]
         (listener)
         (.addEventListener js/window "resize" listener)
         #(.removeEventListener js/window "resize" listener)))
     #js[])
    size))
