(ns ui.core
  (:require
   ["@capgo/capacitor-updater" :refer (CapacitorUpdater)]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [ui.events :as events]
   [ui.lib.datadog.rum]
   [ui.lib.router :refer [static-router browser-router]]
   [ui.lib.apollo :refer (apollo-provider)]
   [ui.lib.platform :refer (platform)]
   [ui.components.loaders.device :rename {loader device-loader}]
   [ui.components.listener :refer (listener)]
   [ui.components.map :refer (global-map)]
   [ui.views.core :refer (views)]))

(when (not= platform "web")
  (.notifyAppReady CapacitorUpdater))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(def router (if (exists? js/document) browser-router static-router))

(defn app []
  [:<>
   [router
    [apollo-provider
     [device-loader
      [listener
       [views]]]]]
   [global-map]])

(def ^:export react-app (r/reactify-component app))

(defn ^:export init []
  (rf/dispatch-sync [::events/initialize-db])
  (rf/clear-subscription-cache!))
