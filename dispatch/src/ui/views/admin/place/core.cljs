(ns ui.views.admin.place.core
  (:require [ui.views.admin.place.list :as list]
            [ui.views.admin.place.create :as create]
            [ui.views.admin.place.detail :as detail]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
