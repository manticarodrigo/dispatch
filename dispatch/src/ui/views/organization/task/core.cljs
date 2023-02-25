(ns ui.views.organization.task.core
  (:require [ui.views.organization.task.list :as list]
            [ui.views.organization.task.detail :as detail]
            [ui.views.organization.task.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
