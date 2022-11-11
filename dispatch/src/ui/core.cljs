(ns ui.core
  (:require
   ["react-dom/client" :refer (createRoot)]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [ui.events :as events]
   [ui.lib.router :refer [browser-router]]
   [ui.lib.apollo :refer (apollo-provider)]
   [ui.components.main :refer (main)]
   [ui.components.routes :refer (routes)]))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn app []
  [browser-router
   [apollo-provider
    [main
     [routes]]]])

(defonce !root (atom nil))

(defn mount []
  (rf/clear-subscription-cache!)
  (reset! !root (createRoot (gdom/getElement "app")))
  (.render ^js @!root (r/as-element [app])))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (mount))

(defn refresh []
  (.unmount ^js @!root)
  (mount))
