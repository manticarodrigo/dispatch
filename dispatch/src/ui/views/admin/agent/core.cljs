(ns ui.views.admin.agent.core
  (:require [ui.views.admin.agent.list :as list]
            [ui.views.admin.agent.detail :as detail]
            [ui.views.admin.agent.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
