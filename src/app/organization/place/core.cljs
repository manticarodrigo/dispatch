(ns app.organization.place.core
  (:require [app.organization.place.list :as list]
            [app.organization.place.create :as create]
            [app.organization.place.detail :as detail]))

(def list-view list/view)
(def detail-view detail/view)
(def create-view create/view)
