(ns ui.components.routes
  (:require [ui.lib.router :as router]
            [ui.views.register :as register]
            [ui.views.login :as login]
            [ui.views.seat.core :as seat]
            [ui.views.address.core :as address]
            [ui.views.route.core :as route]
            [ui.views.stop.core :as stop]
            [ui.views.not-found :as not-found]))

(defn routes []
  [router/routes
   ["/" [:div {:class "flex justify-center items-center w-full h-full"}
         [:p {:class "text-xl"} "Welcome! Please choose a tab."]]]
   ["/register" [register/view]]
   ["/login" [login/view]]
   ["/logout" [router/remove-auth-route]]
   ["/routes" [router/auth-route [route/list-view]]]
   ["/routes/:id" [router/auth-route [route/detail-view]]]
   ["/routes/create" [router/auth-route [route/create-view]]]
   ["/seats" [router/auth-route [seat/list-view]]]
   ["/seats/:id" [router/auth-route [seat/detail-view]]]
   ["/seats/create" [router/auth-route [seat/create-view]]]
   ["/addresses" [router/auth-route [address/list-view]]]
   ["/addresses/:id" [router/auth-route [address/detail-view]]]
   ["/addresses/create" [router/auth-route [address/create-view]]]
   ["/stops/:id" [router/auth-route [stop/detail-view]]]
   ["*" [not-found/view]]])
