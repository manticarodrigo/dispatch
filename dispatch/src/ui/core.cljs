(ns ui.core
  (:require
   ["react-dom/client" :refer (createRoot)]
   ["@capgo/capacitor-updater" :refer (CapacitorUpdater)]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [ui.events :as events]
   [ui.lib.datadog.rum]
   [ui.lib.router :refer [browser-router]]
   [ui.lib.apollo :refer (apollo-provider)]
   [ui.lib.platform :refer (platform)]
   [ui.components.loaders.device :rename {loader device-loader}]
   [ui.components.listener :refer (listener)]
   [ui.components.routes :refer (routes)]))

(when (not= platform "web")
  (.notifyAppReady CapacitorUpdater))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn app []
  [browser-router
   [apollo-provider
    [device-loader
     [listener
      [routes]]]]])

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
