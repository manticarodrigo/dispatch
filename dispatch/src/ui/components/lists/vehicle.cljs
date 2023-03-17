(ns ui.components.lists.vehicle
  (:require [react-feather :rename {Truck VehicleIcon}]
            [ui.components.lists.link-list :refer (link-list)]))

(defn vehicle-list [{:keys [vehicles loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id name weight volume]}]
              {:id id
               :icon VehicleIcon
               :title name
               :subtitle (str weight "kg, " volume "m³")})
            vehicles)}])
