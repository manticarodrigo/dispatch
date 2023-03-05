(ns ui.components.lists.place
  (:require [react-feather :rename {MapPin PinIcon}]
            [ui.components.lists.link-list :refer (link-list)]))

(defn place-list [{:keys [places loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id name description]}]
              {:id id
               :icon PinIcon
               :title name
               :subtitle description})
            places)}])
