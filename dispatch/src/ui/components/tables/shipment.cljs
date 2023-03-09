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
                (for [{:keys [start end]} windows]
                  [:div
                   (str (fmt start) " - " (fmt end))])])))}
   {:id "volume"
    :header (tr [:table.plan/volume])
    :accessorFn #(.. ^js % -size -volume)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 100000) (.toFixed 2))
                  val (.getValue info)]
              (str (fmt val) "m³")))}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(.. ^js % -size -weight)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 1000) (.toFixed 2))
                  val (.getValue info)]
              (str (fmt val) "kg")))}])

(defn shipment-table [{:keys [shipments]}]
  [table {:data shipments
          :columns (get-columns)}])
