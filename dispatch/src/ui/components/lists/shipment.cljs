(ns ui.components.lists.shipment
  (:require [react-feather :rename {Package ShipmentIcon}]
            [ui.utils.i18n :refer (tr)]
            [ui.components.lists.link-list :refer (link-list)]))

(defn shipment-list [{:keys [shipments loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id place size]}]
              (let [{:keys [name]} place]
                {:id id
                 :icon ShipmentIcon
                 :title name
                 :subtitle (str
                            (tr [:misc/volume "Volume "]) (:volume size) ", "
                            (tr [:misc/weight "Weight "]) (:weight size))}))
            shipments)}])
