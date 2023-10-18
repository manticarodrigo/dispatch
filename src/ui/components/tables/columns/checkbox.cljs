(ns ui.components.tables.columns.checkbox
  (:require [reagent.core :as r]
            [ui.components.inputs.checkbox :refer (checkbox)]))

(def checkbox-column
  {:id "select"
   :header (fn [^js info]
             (r/as-element
              [checkbox
               {:checked (-> info .-table .getIsAllRowsSelected)
                :class "block"
                :on-change (-> info .-table .getToggleAllRowsSelectedHandler)}]))
   :cell (fn [^js info]
           (r/as-element
            [checkbox
             {:checked (-> info .-row .getIsSelected)
              :disabled (not (-> info .-row .getCanSelect))
              :class "block"
              :on-change (-> info .-row .getToggleSelectedHandler)}]))})
