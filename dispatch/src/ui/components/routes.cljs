(ns ui.components.routes
  (:require [ui.lib.router :as router]
            [ui.views.register :as register]
            [ui.views.login :as login]
            [ui.views.fleet :as fleet]
            [ui.views.waypoint :as waypoint]
            [ui.views.schedule :as schedule]
            [ui.views.not-found :as not-found]))

(defn routes []
  [router/routes
   ["/register" [register/view]]
   ["/login" [login/view]]
   ["/logout" [router/remove-auth-route]]
   ["/fleet" [router/auth-route [fleet/view]]]
   ["/waypoint" [router/auth-route [waypoint/view]]]
   ["/schedule" [router/auth-route [schedule/view]]]
   ["*" [not-found/view]]])
