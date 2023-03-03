(ns ui.components.lists.vehicle
  (:require [react-feather :rename {Package ShipmentIcon}]
            ;; [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))


(defn vehicle-list [{:keys [vehicles loading]}]
  [:<>
   [:ul
    (doall
     (for [{:keys [id name capacities]} vehicles]
       (let [active? false]
         ^{:key id}
         [:li {:class "mb-2"}
          [link-card {:to id
                      :icon ShipmentIcon
                      :title name
                      :subtitle (str
                                 (tr [:misc/volume "Volume "]) (:volume capacities) ", "
                                 (tr [:misc/weight "Weight "]) (:weight capacities))
                      :detail [status-detail
                               {:active? active?
                                :text (if active?
                                        (tr [:status/active])
                                        (tr [:status/inactive]))}]}]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? vehicles)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
