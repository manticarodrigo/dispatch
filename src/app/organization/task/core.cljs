(ns app.organization.task.core
  (:require [app.organization.task.list :as list]
            [app.organization.task.create :as create]
            [app.organization.task.update :as update]
            [app.organization.task.detail :as detail]))

(def list-view list/view)
(def create-view create/view)
(def update-view update/view)
(def detail-view detail/view)
