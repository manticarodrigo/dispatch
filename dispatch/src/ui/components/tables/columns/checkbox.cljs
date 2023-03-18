(ns ui.components.tables.columns.checkbox
  (:require [reagent.core :as r]))

(defn checkbox [{:keys [checked disabled on-change]}]
  [:input {:type "checkbox"
           :checked checked
           :disabled disabled
           :on-change on-change}])

(def checkbox-column
  {:id "select"
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
              :on-change (-> info .-row .getToggleSelectedHandler)}]))})
