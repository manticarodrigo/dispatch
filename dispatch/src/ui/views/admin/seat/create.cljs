(ns ui.views.admin.seat.create
  (:require [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.seat :refer (seat-form)]))

(defn view []
  [:div {:class padding}
   [title {:title (tr [:view.seat.create/title])}]
   [seat-form]])
