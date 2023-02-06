(ns ui.views.seat.core
  (:require [ui.lib.router :as router]
            [ui.views.seat.layout :refer (layout)]
            [ui.views.seat.route.core :as route]
            [ui.views.seat.stop.core :as stop]))

(def route {:path "seat/:seat"
            :element [layout [router/outlet]]
            :children [{:path "routes" :element [route/list-view]}
                       {:path "routes/:route" :element [route/detail-view]}
                       {:path "stops/:stop" :element [stop/detail-view]}]})
