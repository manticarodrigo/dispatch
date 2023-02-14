(ns ui.components.filters
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.components.inputs.radio-group :refer (radio-group)]))

(defn filters [{:keys [search on-search-change
                       date on-date-change
                       status on-status-change]}]
  [:div {:class "pb-4"}
   [:div {:class "flex justify-between"}
    (when on-search-change
      [input {:aria-label (tr [:field/search])
              :value search
              :placeholder (tr [:field/search])
              :class "w-full"
              :on-text on-search-change}])
    (when (and on-search-change
               on-date-change)
      [:div {:class "w-2"}])
    (when on-date-change
      [date-select {:label (tr [:field/date])
                    :value date
                    :class "w-full"
                    :on-select on-date-change}])]
   (when on-status-change
     [:div {:class "pt-2"}
      [radio-group {:sr-label (tr [:field/status])
                    :value status
                    :options [{:key "ALL" :label (tr [:status/all])}
                              {:key "INCOMPLETE" :label (tr [:status/incomplete])}
                              {:key "COMPLETE" :label (tr [:status/complete])}]
                    :on-change on-status-change}]])])
