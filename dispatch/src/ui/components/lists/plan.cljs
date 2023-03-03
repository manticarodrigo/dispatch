(ns ui.components.lists.plan
  (:require [react-feather :rename {Navigation PlanIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))


(defn plan-list [{:keys [plans loading]}]
  [:div {:class "overflow-y-auto"}
   [:ul
    (doall
     (for [{:keys [id startAt endAt depot vehicles shipments]} plans]
       (let [active? (d/isAfter startAt (js/Date.))]
         ^{:key id}
         [:li
          [link-card {:to id
                      :icon PlanIcon
                      :title (str (d/format startAt "dd/MM hh:mmaaa") " - " (d/format endAt "dd/MM hh:mmaaa"))
                      :subtitle [:div
                                 [:div (str (tr [:misc/leaving-from "Leaving from "]) (:name depot))]
                                 [:div (str (count vehicles) " " (tr [:misc/vehicles "vehicles"]) ", "
                                            (count shipments) " " (tr [:misc/shipments "shipments"]))]]
                      :detail [status-detail
                               {:active? active?
                                :text (if active?
                                        (tr [:status/active])
                                        (tr [:status/inactive]))}]}]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? plans)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
