(ns ui.views.seat.place.core
  (:require [ui.views.seat.place.list :as list]
            [ui.views.seat.place.create :as create]
            [ui.views.seat.place.detail :as detail]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
