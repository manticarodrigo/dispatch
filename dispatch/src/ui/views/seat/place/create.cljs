(ns ui.views.seat.place.create
  (:require [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.place :refer (place-form)]))

(defn view []
  [:div {:class padding}
   [title {:title (tr [:view.place.create/title])}]
   [place-form]])
