(ns ui.views.seat.core
  (:require [ui.lib.router :as router]
            [ui.views.seat.layout :refer (layout)]
            [ui.views.seat.route.core :as route]))

(def route {:path "seat/:id"
            :element [layout [router/outlet]]
            :children [{:path "routes" :element [route/list-view]}
                       {:path "routes/:route" :element [route/detail-view]}]})
