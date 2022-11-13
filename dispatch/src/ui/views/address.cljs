(ns ui.views.address
  (:require [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.forms.location :refer (location-form)]))

(defn view []
  [:div {:class (class-names padding)}
   [location-form]])
