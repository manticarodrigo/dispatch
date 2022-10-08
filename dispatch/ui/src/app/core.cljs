(ns app.core
  (:require
   ["react-router-dom" :refer (BrowserRouter Routes Route)]
   [reagent.core :as r]
   [reagent.dom :as rd]
   [re-frame.core :as rf]
   [app.events :as events]
   [app.views.register.core :refer (register-view)]
   [app.views.login.core :refer (login-view)]
   [app.views.route.core :refer (route-view)]))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn routes-container []
  [:> BrowserRouter
   [:> Routes
    [:> Route {:path "/register" :element (r/as-element [register-view])}]
    [:> Route {:path "/login" :element (r/as-element [login-view])}]
    [:> Route {:path "/route" :element (r/as-element [route-view])}]]])

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rd/unmount-component-at-node root-el)
    (rd/render [routes-container] root-el)))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (mount-root))
