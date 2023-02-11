(ns ui.components.filters
  (:require [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.components.inputs.radio-group :refer (radio-group)]))

(defn filters [{:keys [search on-search-change
                       date on-date-change
                       status on-status-change]}]
  [:div {:class "pb-4"}
   [:div {:class "flex justify-between"}
    (when on-search-change
      [input {:aria-label "Type to search"
              :value search
              :placeholder "Type to search"
              :class "w-full"
              :on-text on-search-change}])
    (when (and on-search-change
               on-date-change)
      [:div {:class "w-2"}])
    (when on-date-change
      [date-select {:label "Select date"
                    :value date
                    :class "w-full"
                    :on-select on-date-change}])]
   (when on-status-change
     [:div {:class "pt-2"}
      [radio-group {:sr-label "Select status"
                    :value status
                    :options [{:key "ALL" :label "All"}
                              {:key "INCOMPLETE" :label "Incomplete"}
                              {:key "COMPLETE" :label "Complete"}]
                    :on-change on-status-change}]])])
