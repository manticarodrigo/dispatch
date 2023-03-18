(ns ui.components.lists.plan
  (:require ["react-feather" :rename {Navigation PlanIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.lists.link-list :refer (link-list)]
            [ui.components.status-detail :refer (status-detail)]))

(defn plan-list [{:keys [plans loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id startAt endAt depot vehicles shipments]}]
              (let [active? (d/isAfter startAt (js/Date.))]
                {:id id
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
                                   (tr [:status/inactive]))}]}))
            plans)}])
