(ns ui.components.routes
  (:require
   [ui.lib.router :as router]
   [ui.subs :refer (listen)]
   [ui.views.register :as register]
   [ui.views.login :as login]
   [ui.views.admin.core :as admin]
   [ui.views.seat.core :as seat]
   [ui.views.not-found :as not-found]))

(defn routes []
  (let [session-id (listen [:session])
        routes (router/use-routes
                [{:index true :element [router/navigate (if session-id "/admin/routes" "/login")]}
                 {:path "register" :element [register/view]}
                 {:path "login" :element [login/view]}
                 {:path "logout" :element [router/remove-auth-route]}
                 admin/route
                 seat/route
                 {:path "*" :element [not-found/view]}])]
    routes))
