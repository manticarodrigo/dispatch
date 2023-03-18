(ns ui.components.tables.vehicle
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]
            [ui.components.tables.columns.checkbox :refer (checkbox-column)]))


(defn get-columns []
  [checkbox-column
   {:id "name"
    :header (tr [:table.vehicle/name])
    :accessorFn #(.. ^js % -name)}
   {:id "weight"
    :header (tr [:table.vehicle/weight])
    :accessorFn #(some-> ^js % .-weight (.toFixed 2))}
   {:id "volume"
    :header (tr [:table.vehicle/volume])
    :accessorFn #(some-> ^js % .-volume (.toFixed 2))}])

(defn vehicle-table [{:keys [data
                             selected-rows
                             set-selected-rows]}]
  [table {:state #js{:rowSelection selected-rows}
          :data data
          :columns (get-columns)
          :enable-row-selection true
          :on-row-selection-change set-selected-rows}])
