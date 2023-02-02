(ns ui.components.routes
  (:require
   [react-router-dom
    :refer (Routes
            Route
            Navigate
            Outlet)]
   [reagent.core :as r]
   [ui.lib.router :as router]
   [ui.subs :refer (listen)]
   [ui.views.register :as register]
   [ui.views.login :as login]
   [ui.views.admin.route.core :as admin-route]
   [ui.views.admin.seat.core :as admin-seat]
   [ui.views.admin.address.core :as admin-address]
   [ui.views.admin.stop.core :as admin-stop]
   [ui.views.seat.route.core :as seat-route]
   [ui.views.not-found :as not-found]))

(defn routes []
  (let [session-id (listen [:session])]
    [:> Routes
     [:> Route {:path "/" :element (r/as-element [:> Navigate {:to (if session-id "/admin/routes" "/login")}])}]
     [:> Route {:path "/register" :element (r/as-element [register/view])}]
     [:> Route {:path "/login" :element (r/as-element [login/view])}]
     [:> Route {:path "/logout" :element (r/as-element [router/remove-auth-route])}]

     [:> Route {:path "/admin" :element (r/as-element [router/auth-route [:> Outlet]])}
      [:> Route {:path "routes" :element (r/as-element [admin-route/list-view])}]
      [:> Route {:path "routes/:id" :element (r/as-element [admin-route/detail-view])}]
      [:> Route {:path "routes/create" :element (r/as-element [admin-route/create-view])}]
      [:> Route {:path "seats" :element (r/as-element [admin-seat/list-view])}]
      [:> Route {:path "seats/:id" :element (r/as-element [admin-seat/detail-view])}]
      [:> Route {:path "seats/create" :element (r/as-element [admin-seat/create-view])}]
      [:> Route {:path "addresses" :element (r/as-element [admin-address/list-view])}]
      [:> Route {:path "addresses/:id" :element (r/as-element [admin-address/detail-view])}]
      [:> Route {:path "addresses/create" :element (r/as-element [admin-address/create-view])}]
      [:> Route {:path "stops/:id" :element (r/as-element [admin-stop/detail-view])}]]

     [:> Route {:path "/seat/:id" :element (r/as-element [:> Outlet])}
      [:> Route {:path "routes" :element (r/as-element [seat-route/list-view])}]]
     [:> Route {:path "*" :element (r/as-element [not-found/view])}]]))
