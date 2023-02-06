(ns ui.views.admin.route.core
  (:require [ui.views.admin.route.list :as list]
            [ui.views.admin.route.detail :as detail]
            [ui.views.admin.route.create :as create]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
