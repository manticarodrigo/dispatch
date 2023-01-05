(ns ui.views.seat.core
  (:require [ui.views.seat.list :as list]
            [ui.views.seat.detail :as detail]
            [ui.views.seat.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)


