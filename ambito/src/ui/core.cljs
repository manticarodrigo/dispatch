(ns ui.core
  (:require
   ["react-dom/client" :refer (createRoot)]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [ui.lib.router :refer [browser-router]]
   [ui.components.icons.ambito :refer [ambito]]))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn app []
  [browser-router
   [:div {:class "flex justify-center items-center w-full h-full"}
    [:div {:class "flex items-center"} [ambito] [:h1 {:class "ml-2 text-2xl"} "Ambito"]]]])

(defonce !root (atom nil))

(defn mount []
  (reset! !root (createRoot (gdom/getElement "app")))
  (.render ^js @!root (r/as-element [app])))

(defn init []
  (mount))

(defn refresh []
  (.unmount ^js @!root)
  (mount))
