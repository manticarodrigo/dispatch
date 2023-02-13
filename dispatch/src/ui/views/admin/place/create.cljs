(ns ui.views.admin.place.create
  (:require [ui.utils.css :refer (padding)]
            [ui.components.forms.place :refer (place-form)]))

(defn view []
  [:div {:class padding}
   [place-form]])
