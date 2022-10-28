(ns ui.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rd]
   [re-frame.core :as rf]
   [ui.events :as events]
   [ui.lib.apollo :refer (apollo-provider)]
   [ui.lib.router :refer (router routes route-auth-wrap)]
   [ui.components.nav :refer (nav)]
   [ui.views.register.core :refer (register-view)]
   [ui.views.login.core :refer (login-view)]
   [ui.views.admin.fleet.core :refer (route-view)]))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn app []
  [apollo-provider
   [router
    [nav
     [routes
      ["/register" [register-view]]
      ["/login" [login-view]]
      ["/admin/fleet" [route-auth-wrap [route-view]]]]]]])

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rd/unmount-component-at-node root-el)
    (rd/render [app] root-el)))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (mount-root))
