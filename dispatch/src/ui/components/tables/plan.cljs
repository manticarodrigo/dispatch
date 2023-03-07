(ns ui.components.tables.plan
  (:require [reagent.core :as r]
            [cljs-bean.core :refer (->clj)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.modal :refer (modal)]
            [ui.components.tables.route :refer (route-table)]))

(defn checkbox [{:keys [checked disabled on-change]}]
  [:input {:type "checkbox"
           :checked checked
           :disabled disabled
           :on-change on-change}])

(defn shipment-cell [^js info]
  (let [!show-modal (r/atom false)]
    (fn []
      (let [{:keys [vehicle shipments]} (->clj (.. info -row -original))]
        [:div {:class "flex justify-between items-center"}
         (count shipments)
         [button {:label "Show"
                  :class "ml-2"
                  :on-click #(reset! !show-modal true)}]
         [modal {:show @!show-modal
                 :title "Shipments"
                 :on-close #(reset! !show-modal false)}
          [:div {:class "overflow-auto w-full h-full"}
           [route-table {:shipments shipments}]]]]))))

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
   {:id "shipments"
    :header (tr [:table.plan/shipments])
    :accessorFn #(count (.. ^js % -shipments))
    :cell (fn [^js info]
            (r/as-element [shipment-cell info]))}
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
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 100000) (.toFixed 2))
                  val (.getValue info)
                  capacity (.. info -row -original -vehicle -capacities -volume)]
              (str (fmt val) " / " (fmt capacity) "m³")))}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(.. ^js % -weight)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 1000) (.toFixed 2))
                  val (.getValue info)
                  capacity (.. info -row -original -vehicle -capacities -weight)]
              (str (fmt val) " / " (fmt capacity) "kg")))}])

(defn plan-table [{:keys [result selected-rows set-selected-rows]}]
  [table {:state #js{:rowSelection selected-rows}
          :data result
          :columns (get-columns)
          :on-row-selection-change set-selected-rows}])
