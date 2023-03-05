(ns ui.components.lists.shipment
  (:require [react-feather :rename {Package ShipmentIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.lists.link-list :refer (link-list)]
            [ui.components.status-detail :refer (status-detail)]))

(defn shipment-list [{:keys [shipments loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id place size windows]}]
              (let [{:keys [name]} place
                    active? (d/isAfter (-> windows first :start js/Date.) (js/Date.))]
                {:id id
                 :icon ShipmentIcon
                 :title name
                 :subtitle (str
                            (tr [:misc/volume "Volume "]) (:volume size) ", "
                            (tr [:misc/weight "Weight "]) (:weight size))
                 :detail [status-detail
                          {:active? active?
                           :text (if active?
                                   (tr [:status/active])
                                   (tr [:status/inactive]))}]}))
            shipments)}])
