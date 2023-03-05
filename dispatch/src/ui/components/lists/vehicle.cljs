(ns ui.components.lists.vehicle
  (:require [react-feather :rename {Truck VehicleIcon}]
            [ui.utils.i18n :refer (tr)]
            [ui.components.lists.link-list :refer (link-list)]))

(defn vehicle-list [{:keys [vehicles loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id name capacities]}]
              {:id id
               :icon VehicleIcon
               :title name
               :subtitle (str
                          (tr [:misc/volume "Volume "]) (:volume capacities) ", "
                          (tr [:misc/weight "Weight "]) (:weight capacities))})
            vehicles)}])
