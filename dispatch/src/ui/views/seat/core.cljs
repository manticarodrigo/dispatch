(ns ui.views.seat.core
  (:require [ui.lib.router :as router]
            [ui.views.seat.layout :refer (layout)]
            [ui.views.seat.task.core :as task]
            [ui.views.seat.place.core :as place]
            [ui.views.seat.stop.core :as stop]))

(def route {:path "seat/:seat"
            :element [layout [router/outlet]]
            :children [{:index true :element [router/navigate "tasks"]}
                       {:path "tasks" :element [task/list-view]}
                       {:path "tasks/:task" :element [task/detail-view]}
                       {:path "places" :element [place/list-view]}
                       {:path "places/:place" :element [place/detail-view]}
                       {:path "places/create" :element [place/create-view]}
                       {:path "stops/:stop" :element [stop/detail-view]}]})
