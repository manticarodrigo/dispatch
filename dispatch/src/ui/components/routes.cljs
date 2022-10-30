(ns ui.components.routes
  (:require [ui.lib.router :as router :refer (router auth-route)]
            [ui.components.main :refer (main)]
            [ui.views.register :as register]
            [ui.views.login :as login]
            [ui.views.fleet :as fleet]
            [ui.views.not-found :as not-found]))

(defn routes []
  [router
   [main
    [router/routes
     ["/register" [register/view]]
     ["/login" [login/view]]
     ["/fleet" [auth-route [fleet/view]]]
     ["*" [not-found/view]]]]])
