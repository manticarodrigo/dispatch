(ns ui.views.organization.plan.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]))

(defn view []
  [map-layout
   [header {:title (tr [:view.plan.create/title])}]])
