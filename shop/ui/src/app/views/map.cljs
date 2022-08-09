(ns app.views.map
  (:require
   [app.components.map :as map]))


(defn page []
  [:div {:class "w-full h-screen bg-blue-600"}
   [map/component]])
