(ns ui.views.seat.route.core
  (:require [ui.views.seat.route.list :as list]
            [ui.views.seat.route.detail :as detail]))

(def list-view list/view)
(def detail-view detail/view)
