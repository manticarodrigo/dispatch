(ns ui.components.routes
  (:require
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
  (let [session-id (listen [:session])
        routes (router/use-routes
                [{:index true :element [router/navigate (if session-id "/admin/routes" "/login")]}
                 {:path "register" :element [register/view]}
                 {:path "login" :element [login/view]}
                 {:path "logout" :element [router/remove-auth-route]}
                 {:path "admin"
                  :element [router/auth-route [router/outlet]]
                  :children [{:path "routes" :element [admin-route/list-view]}
                             {:path "routes/:id" :element [admin-route/detail-view]}
                             {:path "routes/create" :element [admin-route/create-view]}
                             {:path "seats" :element [admin-seat/list-view]}
                             {:path "seats/:id" :element [admin-seat/detail-view]}
                             {:path "seats/create" :element [admin-seat/create-view]}
                             {:path "addresses" :element [admin-address/list-view]}
                             {:path "addresses/:id" :element [admin-address/detail-view]}
                             {:path "addresses/create" :element [admin-address/create-view]}
                             {:path "stops/:id" :element [admin-stop/detail-view]}]}
                 {:path "seat/:id"
                  :element [router/outlet]
                  :children [{:path "routes" :element [seat-route/list-view]}]}
                 {:path "*" :element [not-found/view]}])]
    routes))
