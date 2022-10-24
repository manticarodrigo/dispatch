(ns app.core
  (:require
   ["react-dom/client" :as rc]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [app.events :as events]
   [app.lib.apollo-client :refer (apollo-provider)]
   [app.lib.react-router :refer (router routes route-auth-wrap)]
   [app.views.register.core :refer (register-view)]
   [app.views.login.core :refer (login-view)]
   [app.views.route.core :refer (route-view)]))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn app []
  [apollo-provider
   [router
    [routes
     ["/register" [register-view]]
     ["/login" [login-view]]
     ["/route" [route-auth-wrap [route-view]]]]]])

(defonce root (rc/createRoot
               (.getElementById js/document "app")))

(defn render-root []
  (.render root (r/as-element [app])))

(defn after-load []
  (rf/clear-subscription-cache!)
  (render-root))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (render-root))
