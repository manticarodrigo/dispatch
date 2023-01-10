(ns ui.views.address.core
  (:require [ui.views.address.list :as list]
            [ui.views.address.create :as create]
            [ui.views.address.detail :as detail]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
