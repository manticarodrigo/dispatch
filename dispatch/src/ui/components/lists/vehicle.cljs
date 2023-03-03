(ns ui.components.lists.vehicle
  (:require [react-feather :rename {Truck VehicleIcon}]
            ;; [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]))


(defn vehicle-list [{:keys [vehicles loading]}]
  [:div {:class "overflow-y-auto"}
   [:ul
    (doall
     (for [{:keys [id name capacities]} vehicles]
       ^{:key id}
       [:li
        [link-card {:to id
                    :icon VehicleIcon
                    :title name
                    :subtitle (str
                               (tr [:misc/volume "Volume "]) (:volume capacities) ", "
                               (tr [:misc/weight "Weight "]) (:weight capacities))}]]))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? vehicles)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
