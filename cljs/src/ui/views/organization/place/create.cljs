(ns ui.views.organization.place.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.forms.place :refer (place-form)]))

(defn view []
  [map-layout {:title (tr [:view.place.create/title])}
   [:div {:class "p-4 overflow-y-auto"}
    [place-form]]])
