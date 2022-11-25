(ns ui.components.routes
  (:require [ui.lib.router :as router]
            [ui.views.register :as register]
            [ui.views.login :as login]
            [ui.views.seat.core :as seat]
            [ui.views.address :as address]
            [ui.views.route :as route]
            [ui.views.not-found :as not-found]))

(defn routes []
  [router/routes
   ["/register" [register/view]]
   ["/login" [login/view]]
   ["/logout" [router/remove-auth-route]]
   ["/seats" [router/auth-route [seat/list-view]]]
   ["/seats/:id" [router/auth-route [seat/detail-view]]]
   ["/seats/create" [router/auth-route [seat/create-view]]]
   ["/address" [router/auth-route [address/view]]]
   ["/route" [router/auth-route [route/view]]]
   ["*" [not-found/view]]])
