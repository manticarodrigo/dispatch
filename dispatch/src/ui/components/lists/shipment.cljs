(ns ui.components.lists.shipment
  (:require ["react-feather" :rename {Package ShipmentIcon}]
            [ui.components.lists.link-list :refer (link-list)]))

(defn shipment-list [{:keys [shipments loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id place weight volume]}]
              (let [{:keys [name]} place]
                {:id id
                 :icon ShipmentIcon
                 :title name
                 :subtitle (str weight "kg, " volume "m³")}))
            shipments)}])
