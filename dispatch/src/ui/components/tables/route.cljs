(ns ui.components.tables.route
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]))

(defn get-columns []
  [{:id "order"
    :header "Order"
    :accessorFn (fn [_ idx] (+ idx 1))}
   {:id "name"
    :header "Place"
    :accessorFn #(.. ^js % -place -name)}
   {:id "volume"
    :header (tr [:table.plan/volume])
    :accessorFn #(.. ^js % -size -volume)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 100000) (.toFixed 2))
                  val (.getValue info)
                  capacity (.. info -row -original -size -volume)]
              (str (fmt val) " / " (fmt capacity) "m³")))}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(.. ^js % -size -weight)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 1000) (.toFixed 2))
                  val (.getValue info)
                  capacity (.. info -row -original -size -weight)]
              (str (fmt val) " / " (fmt capacity) "kg")))}])

(defn route-table [{:keys [shipments]}]
  [table {:data shipments
          :columns (get-columns)}])
