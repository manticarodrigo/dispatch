(ns ui.views.seat.core
  (:require [ui.views.seat.list :as list]
            [ui.views.seat.detail :as detail]
            [ui.views.seat.create :as create]
            [ui.views.seat.detail-status :as detail-status]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
(def detail-status-view detail-status/view)


