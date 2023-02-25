(ns ui.views.organization.place.core
  (:require [ui.views.organization.place.list :as list]
            [ui.views.organization.place.create :as create]
            [ui.views.organization.place.detail :as detail]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
