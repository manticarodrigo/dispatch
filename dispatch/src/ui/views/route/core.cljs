(ns ui.views.route.core
  (:require [ui.views.route.list :as list]
            [ui.views.route.create :as create]))

(def list-view list/view)
(def create-view create/view)
