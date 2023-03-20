(ns ui.components.tables.shipment
  (:require [reagent.core :as r]
            [cljs-bean.core :refer (->clj)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]
            [ui.components.tables.columns.checkbox :refer (checkbox-column)]))

(defn get-columns [selected-rows]
  (filterv
   some?
   [(when selected-rows
      checkbox-column)
    {:id "name"
     :header (tr [:table.plan/place])
     :accessorFn #(.. ^js % -place -name)}
    {:id "windows"
     :header (tr [:table.plan/windows])
     :cell (fn [^js info]
             (let [windows (->clj (.. info -row -original -windows))
                   fmt #(-> % js/Date. (d/format "hh:mmaaa"))]
               (r/as-element
                [:div {:class "space-y-2"}
                 (doall
                  (for [[idx {:keys [startAt endAt]}] (map-indexed vector windows)]
                    ^{:key idx}
                    [:div
                     (str (fmt startAt) " - " (fmt endAt))]))])))}
    {:id "volume"
     :header (tr [:table.plan/volume])
     :accessorFn #(some-> ^js % .-volume (.toFixed 2))
     :cell #(str (.getValue ^js %) "m³")}
    {:id "weight"
     :header (tr [:table.plan/weight])
     :accessorFn #(some-> ^js % .-weight (.toFixed 2))
     :cell #(str (.getValue ^js %) "kg")}]))

(defn shipment-table [{:keys [shipments search-term set-search-term selected-rows set-selected-rows]}]
  [table {:state #js{:rowSelection selected-rows}
          :data shipments
          :columns (get-columns selected-rows)
          :search-term search-term
          :set-search-term set-search-term
          :enable-row-selection (some? selected-rows)
          :on-row-selection-change set-selected-rows}])
