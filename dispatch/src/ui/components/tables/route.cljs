(ns ui.components.tables.route
  (:require [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]))

(defn get-columns []
  [{:id "order"
    :header (tr [:table.plan/order])
    :accessorFn (fn [_ idx] (+ idx 1))}
   {:id "name"
    :header (tr [:table.plan/place])
    :accessorFn #(.. ^js % -shipment -place -name)}
   {:id "start"
    :header (tr [:table.plan/start])
    :accessorFn #(-> ^js % .-start js/Date. .getTime)
    :cell #(-> ^js % .getValue js/Date. (d/format "hh:mmaaa"))}
   {:id "volume"
    :header (tr [:table.plan/volume])
    :accessorFn #(.. ^js % -shipment -size -volume)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 100000) (.toFixed 2))
                  val (.getValue info)]
              (str (fmt val) "m³")))}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(.. ^js % -shipment -size -weight)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 1000) (.toFixed 2))
                  val (.getValue info)]
              (str (fmt val) "kg")))}])

(defn route-table [{:keys [visits]}]
  [table {:data visits
          :columns (get-columns)}])
