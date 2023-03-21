(ns ui.components.lists.plan
  (:require ["react-feather" :rename {Navigation PlanIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.lists.link-list :refer (link-list)]))

(defn pill [& children]
  [:span {:class "rounded-full py-1 px-2 text-sm text-neutral-300 bg-neutral-700"}
   (into [:<>] children)])

(defn plan-list [{:keys [plans loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id startAt endAt depot vehicles shipments]}]
              {:id id
               :icon PlanIcon
               :title (str (d/format startAt "dd/MM hh:mmaaa") " - " (d/format endAt "dd/MM hh:mmaaa"))
               :subtitle (str (tr [:misc/leaving-from]) " " (:name depot))
               :detail [:div {:class "space-x-2"}
                        [pill (count vehicles) " " (tr [:noun/vehicles])]
                        [pill (count shipments) " " (tr [:noun/shipments])]]})
            plans)}])
