(ns ui.core
  (:require
   [react]
   ["react-dom/client" :refer (createRoot)]
   ["@capgo/capacitor-updater" :refer (CapacitorUpdater)]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [ui.events :as events]
   [ui.lib.aws.rum]
   [ui.lib.router :refer [browser-router]]
   [ui.lib.apollo :refer (apollo-provider)]
   [ui.lib.platform :refer (platform)]
   [ui.lib.google.maps.overlay :refer (update-overlay)]
   [ui.hooks.use-location :refer (use-location)]
   [ui.components.main :refer (main)]
   [ui.components.routes :refer (routes)]))

(when (not= platform "web")
  (.notifyAppReady CapacitorUpdater))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn app []
  (let [watch-location (use-location)]
    (react/useEffect
     (fn []
       (watch-location
        (fn [location]
          (update-overlay {:lat (:latitude location)
                           :lng (:longitude location)})))
       #())
     #js[])
    [browser-router
     [apollo-provider
      [main
       [routes]]]]))

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
