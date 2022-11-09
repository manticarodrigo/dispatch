(ns ui.views.schedule
  (:require
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.forms.route :refer (route-form)]))

(defn view []
  [:div {:class (class-names padding)}
   [route-form]])
