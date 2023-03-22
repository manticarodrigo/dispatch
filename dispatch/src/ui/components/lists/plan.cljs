(ns ui.components.lists.plan
  (:require ["react-feather" :rename {Navigation PlanIcon
                                      Truck VehicleIcon
                                      Package ShipmentIcon}]
            [clojure.string :as s]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.lists.link-list :refer (link-list)]))

(defn pill [icon text]
  [:span {:class "rounded-full border border-neutral-600 py-1 px-3 text-sm text-neutral-100 font-light bg-neutral-800"}
   [:> icon {:class "inline mr-2 w-4 h-4 text-neutral-400"}]
   text])

(defn plan-list [{:keys [plans loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id startAt endAt depot vehicles shipments]}]
              {:id id
               :icon PlanIcon
               :title (if (d/isSameDay startAt endAt)
                        (str (d/format startAt "hh:mm aaa") " - " (d/format endAt "hh:mm aaa"))
                        (str (s/capitalize (d/formatRelative startAt (js/Date.)))
                             " - "
                             (s/capitalize (d/formatRelative endAt (js/Date.)))))
               :subtitle (str (tr [:misc/leaving-from]) " " (:name depot))
               :detail [:div {:class "space-x-2"}
                        [pill VehicleIcon (count vehicles)]
                        [pill ShipmentIcon (count shipments)]]})
            plans)}])
