(ns ui.views.organization.task.core
  (:require [ui.views.organization.task.list :as list]
            [ui.views.organization.task.create :as create]
            [ui.views.organization.task.update :as update]
            [ui.views.organization.task.detail :as detail]))

(def list-view list/view)
(def create-view create/view)
(def update-view update/view)
(def detail-view detail/view)
