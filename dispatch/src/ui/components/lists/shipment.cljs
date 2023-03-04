(ns ui.components.lists.shipment
  (:require [react-feather :rename {Package ShipmentIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))


(defn shipment-list [{:keys [shipments loading]}]
  [:div {:class "overflow-y-auto"}
   [:ul
    (doall
     (for [{:keys [id place size windows]} shipments]
       (let [{:keys [name]} place
             active? (d/isAfter (-> windows first :start js/Date.) (js/Date.))]
         ^{:key id}
         [:li
          [link-card {:to id
                      :icon ShipmentIcon
                      :title name
                      :subtitle (str
                                 (tr [:misc/volume "Volume "]) (:volume size) ", "
                                 (tr [:misc/weight "Weight "]) (:weight size))
                      :detail [status-detail
                               {:active? active?
                                :text (if active?
                                        (tr [:status/active])
                                        (tr [:status/inactive]))}]}]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? shipments)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
