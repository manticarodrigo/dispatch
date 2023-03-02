(ns ui.views.organization.shipment.create
  (:require [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]))

(defn view []
  [:div {:class padding}
   [title {:title (tr [:view.shipment.create/title])}]])
