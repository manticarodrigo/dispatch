(ns app.components.gmap
  (:require [app.hooks.gmap :as gmap]))


(defn- map-component []
  (let [!el (gmap/hook)]
    [:div
     {:ref (fn [el] (reset! !el el))
      :class "w-full h-full"}]))

(defn component []
  [:f> map-component])
