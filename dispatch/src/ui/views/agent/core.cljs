(ns ui.views.agent.core
  (:require [ui.lib.router :as router]
            [ui.views.agent.layout :refer (layout)]
            [ui.views.agent.task.core :as task]
            [ui.views.agent.place.core :as place]
            [ui.views.agent.stop.core :as stop]))

(def route {:path "agent/:agent"
            :element [layout [router/outlet]]
            :children [{:index true :element [router/navigate "tasks"]}
                       {:path "tasks" :element [task/list-view]}
                       {:path "tasks/:task" :element [task/detail-view]}
                       {:path "places" :element [place/list-view]}
                       {:path "places/:place" :element [place/detail-view]}
                       {:path "places/create" :element [place/create-view]}
                       {:path "stops/:stop" :element [stop/detail-view]}]})
