(ns ui.views.address.create
  (:require [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.forms.address :refer (address-form)]))

(defn view []
  [:div {:class (class-names padding)}
   [address-form]])
