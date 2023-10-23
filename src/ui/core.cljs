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

(r/set-default-compiler! (r/create-compiler {:function-components true}))

(def router (if (exists? js/document) browser-router static-router))

(defn app []
  [router
   [views]])

(defn providers [& children]
  [:<>
   [apollo-provider
    [device-loader
     [listener
      (into [:<>] children)]]]
   [global-map]])

(def ^:export Views (r/reactify-component app))
(def ^:export Providers (r/reactify-component providers))

(defn ^:export init []
  (rf/dispatch-sync [::events/initialize-db])
  (rf/clear-subscription-cache!))
