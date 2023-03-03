(ns ui.views.organization.plan.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.title :refer (title)]))

(defn view []
  [map-layout
   [title {:title (tr [:view.plan.create/title])}]])
