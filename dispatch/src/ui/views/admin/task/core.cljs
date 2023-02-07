(ns ui.views.admin.task.core
  (:require [ui.views.admin.task.list :as list]
            [ui.views.admin.task.detail :as detail]
            [ui.views.admin.task.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
