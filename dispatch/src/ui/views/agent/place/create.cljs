(ns ui.views.agent.place.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]
            [ui.components.forms.place :refer (place-form)]))

(defn view []
  [map-layout
   [header {:title (tr [:view.place.create/title])}]
   [:div {:class "p-4 overflow-y-auto"}
    [place-form]]])
