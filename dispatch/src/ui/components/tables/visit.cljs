(ns ui.components.tables.visit
  (:require [cljs-bean.core :refer (->clj)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]))

(defn get-columns []
  [{:id "order"
    :header (tr [:table.plan/order])
    :accessorFn (fn [_ idx] (+ idx 1))}
   {:id "name"
    :header (tr [:table.plan/place])
    :accessorFn #(-> (or
                      (.. ^js % -depot)
                      (.. ^js % -shipment -place)) .-name)}
   {:id "arrival"
    :header (tr [:table.plan/arrival])
    :accessorFn #(-> ^js % .-arrival js/Date. .getTime)
    :cell #(-> ^js % .getValue js/Date. (d/format "hh:mmaaa"))}
   {:id "volume"
    :header (tr [:table.plan/volume])
    :accessorFn #(some-> ^js % .-shipment .-size .-volume)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 100000) (.toFixed 2))
                  val (.getValue info)]
              (str (fmt val) "m³")))}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(some-> ^js % .-shipment .-size .-weight)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 1000) (.toFixed 2))
                  val (.getValue info)]
              (str (fmt val) "kg")))}])

(defn visit-table [{:keys [visits]}]
  [table {:data visits
          :columns (get-columns)}])
