(ns ui.components.tables.shipment
  (:require [reagent.core :as r]
            [cljs-bean.core :refer (->clj)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]))

(defn get-columns []
  [{:id "name"
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
                 (for [[idx {:keys [start end]}] (map-indexed vector windows)]
                   ^{:key idx}
                   [:div
                    (str (fmt start) " - " (fmt end))]))])))}
   {:id "volume"
    :header (tr [:table.plan/volume])
    :accessorFn #(some-> ^js % .-volume (.toFixed 2))
    :cell #(str (.getValue ^js %) "m³")}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(some-> ^js % .-weight (.toFixed 2))
    :cell #(str (.getValue ^js %) "kg")}])

(defn shipment-table [{:keys [shipments]}]
  [table {:data shipments
          :columns (get-columns)}])
