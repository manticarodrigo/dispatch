(ns ui.components.tables.plan
  (:require [react :refer (useState)]
            [reagent.core :as r]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]))

(defn checkbox [{:keys [checked disabled on-change]}]
  [:input {:type "checkbox"
           :checked checked
           :disabled disabled
           :on-change on-change}])

(defn get-columns []
  [{:id "select"
    :header (fn [^js info]
              (r/as-element
               [checkbox
                {:checked (-> info .-table .getIsAllRowsSelected)
                 :on-change (-> info .-table .getToggleAllRowsSelectedHandler)}]))
    :cell (fn [^js info]
            (r/as-element
             [checkbox
              {:checked (-> info .-row .getIsSelected)
               :disabled (not (-> info .-row .getCanSelect))
               :on-change (-> info .-row .getToggleSelectedHandler)}]))}
   {:id "vehicle"
    :header (tr [:table.plan/vehicle])
    :accessorFn #(.. ^js % -vehicle -name)}
   {:id "start"
    :header (tr [:table.plan/start])
    :accessorFn #(-> (.. ^js % -start) js/Date. .getTime)
    :cell #(-> ^js % .getValue js/Date. (d/format "hh:mmaaa"))}
   {:id "end"
    :header (tr [:table.plan/end])
    :accessorFn #(-> (.. ^js % -end) js/Date. .getTime)
    :cell #(-> ^js % .getValue js/Date. (d/format "hh:mmaaa"))}
   {:id "meters"
    :header (tr [:table.plan/meters])
    :accessorFn #(.. ^js % -meters)}
   {:id "volume"
    :header (tr [:table.plan/volume])
    :accessorFn #(.. ^js % -volume)
    :cell (fn [^js cell]
            (let [fmt #(-> % (/ 100000) (.toFixed 2))
                  val (.getValue cell)
                  capacity (.. cell -row -original -vehicle -capacities -volume)]
              (str (fmt val) " / " (fmt capacity) "m³")))}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(.. ^js % -weight)
    :cell (fn [^js cell]
            (let [fmt #(-> % (/ 1000) (.toFixed 2))
                  val (.getValue cell)
                  capacity (.. cell -row -original -vehicle -capacities -weight)]
              (str (fmt val) " / " (fmt capacity) "kg")))}
   {:id "shipments"
    :header (tr [:table.plan/shipments])
    :accessorFn #(count (.. ^js % -shipments))}])

(defn plan-table [{:keys [result]}]
  (let [[selected-rows set-selected-rows] (useState #js{})]
    [table {:state #js{:rowSelection selected-rows}
            :data result
            :columns (get-columns)
            :on-row-selection-change set-selected-rows}]))
