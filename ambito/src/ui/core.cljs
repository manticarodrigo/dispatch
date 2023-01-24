(ns ui.core
  (:require
   ["react-dom/client" :refer (createRoot)]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [ui.lib.router :refer [browser-router routes]]
   [ui.views.privacy :as privacy]
   [ui.views.home :as home]))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn app []
  [browser-router
   [routes
    ["/privacy" [privacy/view]]
    ["*" [home/view]]]])

(defonce !root (atom nil))

(defn mount []
  (reset! !root (createRoot (gdom/getElement "app")))
  (.render ^js @!root (r/as-element [app])))

(defn init []
  (mount))

(defn refresh []
  (.unmount ^js @!root)
  (mount))
