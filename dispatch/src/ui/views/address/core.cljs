(ns ui.views.address.core
  (:require [ui.views.address.list :as list]
            [ui.views.address.create :as create]))

(def list-view list/view)
(def create-view create/view)
