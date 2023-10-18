(ns ui.views.organization.agent.core
  (:require [ui.views.organization.agent.list :as list]
            [ui.views.organization.agent.detail :as detail]
            [ui.views.organization.agent.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
