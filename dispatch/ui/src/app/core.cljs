(ns app.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rd]
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

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rd/unmount-component-at-node root-el)
    (rd/render [app] root-el)))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (mount-root))
