(ns ui.views.admin.place.create
  (:require [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.forms.place :refer (place-form)]))

(defn view []
  [:div {:class (class-names padding)}
   [place-form]])
