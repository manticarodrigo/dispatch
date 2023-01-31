(ns ui.views.admin.address.core
  (:require [ui.views.admin.address.list :as list]
            [ui.views.admin.address.create :as create]
            [ui.views.admin.address.detail :as detail]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
