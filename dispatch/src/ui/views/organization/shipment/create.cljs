(ns ui.views.organization.shipment.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]))

(defn view []
  [map-layout
   [header {:title (tr [:view.shipment.create/title])}]])
