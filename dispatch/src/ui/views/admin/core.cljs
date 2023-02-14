(ns ui.views.admin.core
  (:require [ui.lib.router :as router]
            [ui.views.admin.task.core :as task]
            [ui.views.admin.agent.core :as agent]
            [ui.views.admin.place.core :as place]
            [ui.views.admin.stop.core :as stop]))

(def route {:path "admin"
            :element [router/auth-route [router/outlet]]
            :children [{:path "tasks" :element [task/list-view]}
                       {:path "tasks/:task" :element [task/detail-view]}
                       {:path "tasks/create" :element [task/create-view]}
                       {:path "agents" :element [agent/list-view]}
                       {:path "agents/:agent" :element [agent/detail-view]}
                       {:path "agents/create" :element [agent/create-view]}
                       {:path "places" :element [place/list-view]}
                       {:path "places/:place" :element [place/detail-view]}
                       {:path "places/create" :element [place/create-view]}
                       {:path "stops/:stop" :element [stop/detail-view]}]})
