(ns ui.views.core
  (:require [app.not-found :as not-found]
            [ui.lib.router :as router]
            [ui.views.agent.core :as agent]))

(defn views []
  (router/use-routes
   [agent/route
    {:path "*" :element [not-found/view]}]))
