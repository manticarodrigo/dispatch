(ns ui.views.agent.place.core
  (:require [ui.views.agent.place.list :as list]
            [ui.views.agent.place.create :as create]
            [ui.views.agent.place.detail :as detail]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
