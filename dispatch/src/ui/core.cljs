(ns ui.core
  (:require
   ["react-dom/client" :refer (createRoot)]
   ["@datadog/browser-rum" :refer (datadogRum)]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [cljs-bean.core :refer (->js)]
   [ui.config :as config]
   [ui.events :as events]
   [ui.lib.router :refer [browser-router]]
   [ui.lib.apollo :refer (apollo-provider)]
   [ui.components.main :refer (main)]
   [ui.components.routes :refer (routes)]))

(when config/STAGE
  (.init
   datadogRum
   (->js {:applicationId "4afda6c4-4568-4cb5-8ceb-35a7f4267572"
          :clientToken "pube6b39edae8d33b6565be537b13f40e1d"
          :site "datadoghq.com"
          :service "ui"
          :env config/STAGE
          :version config/VERSION
          :sampleRate 100
          :sessionReplaySampleRate 20
          :trackInteractions true
          :trackResources true
          :trackLongTasks true
          :allowedTracingOrigins #js[config/API_URL]})))

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
