(ns ui.components.tables.vehicle
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]
            [ui.components.tables.columns.checkbox :refer (checkbox-column)]))

(defn vehicle-table [{:keys [vehicles
                             search-term
                             set-search-term
                             selected-rows
                             set-selected-rows]}]
  [table {:state #js{:rowSelection selected-rows}
          :data vehicles
          :columns (filter some?
                           [(when set-selected-rows checkbox-column)
                            {:id "name"
                             :header (tr [:table.vehicle/name])
                             :accessorFn #(.. ^js % -name)}
                            {:id "weight"
                             :header (tr [:table.vehicle/weight])
                             :accessorFn #(some-> ^js % .-weight (.toFixed 2))}
                            {:id "volume"
                             :header (tr [:table.vehicle/volume])
                             :accessorFn #(some-> ^js % .-volume (.toFixed 2))}])
          :search-term search-term
          :set-search-term set-search-term
          :enable-row-selection true
          :on-row-selection-change set-selected-rows}])
