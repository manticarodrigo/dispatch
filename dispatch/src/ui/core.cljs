(ns ui.core
  (:require
   ["react-dom/client" :refer (createRoot)]
   ["aws-rum-web" :refer (AwsRum)]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [ui.config :as config]
   [ui.events :as events]
   [ui.lib.router :refer [browser-router]]
   [ui.lib.apollo :refer (apollo-provider)]
   [ui.components.main :refer (main)]
   [ui.components.routes :refer (routes)]))

(when config/RUM_MONITOR_ID
  (AwsRum.
   config/RUM_MONITOR_ID
   config/VERSION
   "us-east-1"
   #js{:sessionSampleRate 1
       :endpoint "https://dataplane.rum.us-east-1.amazonaws.com"
       :guestRoleArn config/RUM_GUEST_ROLE_ARN
       :identityPoolId config/RUM_IDENTITY_POOL_ID
       :telemetries #js["errors"
                        "performance"
                        #js["http" #js{:urlsToInclude #js[(re-pattern config/API_URL)]
                                       :addXRayTraceIdHeader true}]]
       :allowCookies true
       :enableXRay true}))

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
