(ns ui.views.organization.agent.core
  (:require [ui.views.organization.agent.list :as list]
            [ui.views.organization.agent.detail :as detail]
            [ui.views.organization.agent.locations :as locations]
            [ui.views.organization.agent.performance :as performance]
            [ui.views.organization.agent.create :as create]))

(def list-view list/view)
(def performance-view performance/view)
(def detail-view detail/view)
(def locations-view locations/view)
(def create-view create/view)