(ns ui.views.organization.plan.core
  (:require [ui.views.organization.plan.list :as list]
            [ui.views.organization.plan.detail :as detail]
            [ui.views.organization.plan.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
