(ns ui.views.admin.seat.core
  (:require [ui.views.admin.seat.list :as list]
            [ui.views.admin.seat.detail :as detail]
            [ui.views.admin.seat.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
