(ns ui.views.organization.shipment.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]))

(defn view []
  [map-layout {:title (tr [:view.shipment.create/title])}])
