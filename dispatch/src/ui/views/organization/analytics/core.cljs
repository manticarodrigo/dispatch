(ns ui.views.organization.analytics.core
  (:require [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.visualizations.pie-chart :refer (pie-chart)]
            [ui.components.visualizations.area-chart :refer (area-chart)]))

(defn gen-data []
  (map
   (fn [idx]
     {:x (d/addDays (js/Date.) idx)
      :y (rand-int 100)})
   (range 0 30)))

(def charts
  [{:title "Warehouse operating cost"
    :subtitle "$23,045.45"
    :content [pie-chart {:data [{:label "Rent" :value 1000}
                                {:label "Utilities" :value 500}
                                {:label "Labor" :value 2000}
                                {:label "Maintenance" :value 1000}
                                {:label "Other" :value 500}]}]}
   {:title "Perfect order rate"
    :subtitle "95.6%"
    :content [area-chart {:format-x #(str % "%")
                          :data (gen-data)}]}
   {:title "Total shipments by customer"}
   {:title "On-time shipments"}])

(defn card [title subtitle content]
  [:div {:class "rounded bg-zinc-800/50"}
   [:div {:class "flex items-center mb-2 p-4 text-sm font-medium text-neutral-300"}
    title
    (when subtitle
      [:div {:class "ml-2 py-1 px-2 rounded-full text-zinc-50 bg-zinc-900"}
       subtitle])]
   [:div {:class "p-4 flex flex-col h-[300px]"}
    content]])

(defn view []
  [bare-layout {:title (tr [:view.analytics/title "Analytics"])}
   [:div {:class "p-4 grid auto-rows-min grid-cols-1 lg:grid-cols-2 gap-4 h-full w-full overflow-auto"}
    (for [[idx {:keys [title subtitle content]}] (map-indexed vector charts)]
      ^{:key idx}
      [card title subtitle content])]])
