(ns ui.views.organization.vehicle.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]))

(defn view []
  [map-layout {:title (tr [:view.vehicle.create/title])}])
