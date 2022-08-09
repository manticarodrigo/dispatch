(ns app.components.map
  (:require [app.hooks.use-map :as use-map]))


(defn map-component []
  (let [!map-el (use-map/hook)]
    [:capacitor-google-map
     {:ref (fn [el] (reset! !map-el el))
      :style {:display "block"
              :width "100%"
              :height "100%"}}]))

(defn component []
  [:f> map-component])
