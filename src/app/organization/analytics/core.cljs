(ns app.organization.analytics.core
  (:require [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.visualizations.pie-chart :refer (pie-chart)]
            [ui.components.visualizations.area-chart :refer (area-chart)]))

(def perfect-visit-rate-data
  (map
   (fn [idx]
     {:x (d/addDays (js/Date.) idx)
      :y (+ 90 (rand-int 10))})
   (range 0 30)))

(def visit-revenue-per-gas-liter-data
  (map
   (fn [idx]
     {:x (d/addDays (js/Date.) idx)
      :y (+ 90 (rand 10))})
   (range 0 30)))

(defn total-sum [coll]
  (reduce (fn [acc item] (+ acc (:value item))) 0 coll))

(defn value-to-percent [total item]
  (assoc item :percent (* 100 (/ (:value item) total))))

(def customer-data
  (let [data [{:label "Customer 1" :value 1000}
              {:label "Customer 2" :value 500}
              {:label "Customer 3" :value 2000}
              {:label "Customer 4" :value 1000}
              {:label "Customer 5" :value 500}]
        total (total-sum data)]
    (->> data
         (map (partial value-to-percent total))
         (sort-by :percent)
         (reverse))))

(def time-utilization-data
  (let [data [{:label "Driving" :value 800}
              {:label "Unloading" :value 400}
              {:label "Loading" :value 300}
              {:label "Idle" :value 100}
              {:label "Break" :value 50}]]
    (->> data
         (map (partial value-to-percent (total-sum data)))
         (sort-by :percent)
         (reverse))))

(def vehicle-utilization-data
  (let [data [{:label "Weight" :value 95}
              {:label "Volume" :value 89}]]
    (->> data
         (map (partial value-to-percent 100))
         (sort-by :percent)
         (reverse))))

(def charts
  [{:title "Perfect visit rate"
    :subtitle "95.6%"
    :content [area-chart {:format-x #(str % "%")
                          :data perfect-visit-rate-data}]}
   {:title (tr [:view.analytics.charts.revenue-per-gas-liter/title])
    :subtitle "$91.23"
    :content [area-chart {:format-x-axis #(str "$" (js/Math.round %))
                          :format-x #(str "$" (.toFixed % 2))
                          :data visit-revenue-per-gas-liter-data}]}
   {:title "Time utilization"
    :content [pie-chart {:data time-utilization-data}]}
   {:title "Customer visits"
    :content [pie-chart {:data customer-data}]}])

(def kpis
  [{:title "Distance traveled"
    :value "12,764km"
    :change 0.2}
   {:title "Fuel consumed"
    :value "1,234L"
    :change -0.15}
   {:title "Completed visits"
    :value "450"
    :change 0.25}
   {:title "Incomplete visits"
    :value "12"
    :change 0.2}
   {:title "Avg visit duration"
    :value "24m"
    :change -0.1}
   {:title "Avg vehicle utilization"
    :value "98%"
    :change 0.1}])

(defn card [title subtitle content]
  [:div {:class "rounded bg-zinc-800/25 shadow"}
   [:div {:class "flex items-center p-4 text-sm font-medium text-neutral-300"}
    title
    (when subtitle
      [:div {:class "ml-2 py-1 px-3 rounded-full text-zinc-50 bg-slate-700"}
       subtitle])]
   [:div {:class "px-4 pb-4 flex flex-col"}
    content]])

(defn view []
  [bare-layout {:title (tr [:view.analytics/title "Analytics"])}
   [:div {:class "p-4 overflow-auto"}
    [:div {:class "pb-4 grid auto-rows-min grid-cols-2 xl:grid-cols-3 2xl:grid-cols-6 gap-4"}
     (for [{:keys [title value change]} kpis]
       ^{:key title}
       [card title value
        [:div {:class "text-lg font-bold text-neutral-300"}
         (str (if (pos? change) "+" "") (int (* 100 change)) "%")]])]
    [:div {:class "grid auto-rows-min grid-cols-1 lg:grid-cols-2 gap-4"}
     (for [[idx {:keys [title subtitle content]}] (map-indexed vector charts)]
       ^{:key idx}
       [card title subtitle
        [:div {:class "h-[300px]"} content]])]]])
