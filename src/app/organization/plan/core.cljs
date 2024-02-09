(ns app.organization.plan.core
  (:require [app.organization.plan.list :as list]
            [app.organization.plan.detail :as detail]
            [app.organization.plan.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
