(ns ui.components.forms.location
  (:require [reagent.core :as r]
            [ui.subs :refer (listen)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.string :refer (class-names)]
            [ui.hooks.use-route :refer (use-route-context)]
            [ui.components.inputs.generic.combobox :refer (combobox)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.button :refer (button)]))

(defn location-form [{initial-state :initial-state on-submit :on-submit}]
  (let [!state (r/atom (or initial-state {}))
        !anoms (r/atom {})
        search (listen [:search])
        {search-address :search
         set-origin :origin}
        (use-route-context)]
    (fn []
      [:<>
       [:form {:class "flex flex-col"
               :on-submit on-submit}
        [input {:id "name"
                :label "Name"
                :value (:name @!state)
                :required true
                :class "pb-4"
                :on-text #(swap! !state assoc :name %)}]
        [combobox {:label "Location"
                   :class "pb-4"
                   :options search
                   :option-to-label #(:description %)
                   :option-to-value #(:place_id %)
                   :on-text search-address
                   :on-change set-origin}]
        [button {:label "Submit" :class "my-4"}]
        (doall (for [anom @!anoms]
                 [:span {:key (:reason anom)
                         :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                  (tr-error anom)]))]])))
