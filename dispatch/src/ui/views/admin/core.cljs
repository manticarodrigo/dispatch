(ns ui.views.admin.core
  (:require [ui.lib.router :as router]
            [ui.views.admin.task.core :as task]
            [ui.views.admin.seat.core :as seat]
            [ui.views.admin.place.core :as place]
            [ui.views.admin.waypoint.core :as waypoint]))

(def route {:path "admin"
            :element [router/auth-route [router/outlet]]
            :children [{:path "tasks" :element [task/list-view]}
                       {:path "tasks/:task" :element [task/detail-view]}
                       {:path "tasks/create" :element [task/create-view]}
                       {:path "seats" :element [seat/list-view]}
                       {:path "seats/:seat" :element [seat/detail-view]}
                       {:path "seats/create" :element [seat/create-view]}
                       {:path "places" :element [place/list-view]}
                       {:path "places/:place" :element [place/detail-view]}
                       {:path "places/create" :element [place/create-view]}
                       {:path "waypoints/:waypoint" :element [waypoint/detail-view]}]})
