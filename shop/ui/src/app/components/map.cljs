(ns app.components.map
  (:require [app.hooks.use-map :as use-map]))


(defn map-component []
  (let [!map-el (use-map/hook)]
    [:div
     {:ref (fn [el] (reset! !map-el el))
      :class "w-full h-full"}]))

(defn component []
  [:f> map-component])
