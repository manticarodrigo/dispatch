(ns ui.components.routes
  (:require
   [ui.lib.router :as router]
   [ui.views.register :as register]
   [ui.views.login :as login]
   [ui.views.login-confirm :as login-confirm]
   [ui.views.organization.core :as organization]
   [ui.views.agent.core :as agent]
   [ui.views.not-found :as not-found]
   [ui.components.loaders.scope :rename {loader scope-loader}]))

(defn routes []
  (router/use-routes
   [{:index true :element [scope-loader]}
    {:path "register" :element [register/view]}
    {:path "login" :element [login/view]}
    {:path "login/confirm" :element [login-confirm/view]}
    {:path "logout" :element [router/remove-auth-route]}
    organization/route
    agent/route
    {:path "*" :element [not-found/view]}]))
