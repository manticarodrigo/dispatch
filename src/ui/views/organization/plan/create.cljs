(ns ui.views.organization.plan.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]))

(defn view []
  [map-layout {:title (tr [:view.plan.create/title])}])
