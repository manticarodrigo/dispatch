(ns ui.views.admin.seat.create
  (:require [ui.utils.css :refer (padding)]
            [ui.components.forms.seat :refer (seat-form)]))

(defn view []
  [:div {:class padding}
   [seat-form]])
