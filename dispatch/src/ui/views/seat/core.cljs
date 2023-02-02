(ns ui.views.seat.core
  (:require [ui.lib.router :as router]
            [ui.views.seat.route.core :as route]))

(def route {:path "seat/:id"
            :element [router/outlet]
            :children [{:path "routes" :element [route/list-view]}]})
