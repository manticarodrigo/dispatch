(ns ui.views.seat.create
  (:require [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.forms.seat :refer (seat-form)]))

(defn view []
  [:div {:class (class-names padding)}
   [seat-form]])
