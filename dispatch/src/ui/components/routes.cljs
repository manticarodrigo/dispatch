(ns ui.components.routes
  (:require [ui.lib.router :as router]
            [ui.subs :refer (listen)]
            [ui.views.register :as register]
            [ui.views.login :as login]
            [ui.views.admin.route.core :as route]
            [ui.views.admin.seat.core :as seat]
            [ui.views.admin.address.core :as address]
            [ui.views.admin.stop.core :as stop]
            [ui.views.not-found :as not-found]))

(defn routes []
  (let [session-id (listen [:session])]
    [router/routes
     ["/" [router/navigate-route (if session-id "/admin/routes" "/login")]]
     ["/register" [register/view]]
     ["/login" [login/view]]
     ["/logout" [router/remove-auth-route]]
     ["/admin/routes" [router/auth-route [route/list-view]]]
     ["/admin/routes/:id" [router/auth-route [route/detail-view]]]
     ["/admin/routes/create" [router/auth-route [route/create-view]]]
     ["/admin/seats" [router/auth-route [seat/list-view]]]
     ["/admin/seats/:id" [router/auth-route [seat/detail-view]]]
     ["/admin/seats/create" [router/auth-route [seat/create-view]]]
     ["/admin/addresses" [router/auth-route [address/list-view]]]
     ["/admin/addresses/:id" [router/auth-route [address/detail-view]]]
     ["/admin/addresses/create" [router/auth-route [address/create-view]]]
     ["/admin/stops/:id" [router/auth-route [stop/detail-view]]]
     ["*" [not-found/view]]]))
