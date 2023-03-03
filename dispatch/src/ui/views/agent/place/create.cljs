(ns ui.views.agent.place.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.title :refer (title)]
            [ui.components.forms.place :refer (place-form)]))

(defn view []
  [map-layout
   [title {:title (tr [:view.place.create/title])}]
   [place-form]])
