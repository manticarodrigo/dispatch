(ns ui.components.forms.route
  (:require [react-feather
             :rename {AlignJustify DragIcon
                      ChevronUp ChevronUpIcon
                      ChevronDown ChevronDownIcon}]
            [reagent.core :as r]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.generic.combobox :refer (combobox)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.button :refer (button base-button-class)]))


(defn route-form [{initial-state :initial-state on-submit :on-submit}]
  (let [!state (r/atom (or initial-state {:waypoints [{:name "Subway" :location "Galerias"}
                                                      {:name "Pizza Hut" :location "Galerias"}
                                                      {:name "La Colonia" :location "Galerias"}]}))
        !anoms (r/atom {})]
    (fn []
      [:<>
       [:form {:class "flex flex-col"
               :on-submit on-submit}
        [combobox {:label "Assigned seat"
                   :class "pb-4"
                   :options [{:label "Foo" :value "foo"}
                             {:label "Bar" :value "bar"}]
                   :option-to-label #(:label %)
                   :option-to-value #(:value %)
                   :on-text (fn [])
                   :on-change (fn [])}]
        [input {:id "origin"
                :label "Origin address"
                :value (:username @!state)
                :required true
                :class "pb-4"
                :on-text #(swap! !state assoc :origin %)}]
        [input {:id "start"
                :label "Departure time"
                :type "datetime-local"
                :value (:start @!state)
                :required true
                :class "pb-4"
                :on-text #(swap! !state assoc :start %)}]
        [:label {:class "mb-2 text-sm"} "Manage waypoints"]
        [:div
         [combobox {:aria-label "Add waypoint"
                    :placeholder "Search for waypoints"
                    :class "mb-4"
                    :options [{:label "Foo" :value "foo"}
                              {:label "Bar" :value "bar"}]
                    :option-to-label #(:label %)
                    :option-to-value #(:value %)
                    :on-text (fn [])
                    :on-change (fn [])}]
         [:ol
          (doall (for [[idx point] (map-indexed vector (-> @!state :waypoints))]
                   [:li {:class (class-names "flex items-center mb-2 rounded p-2 bg-neutral-900" base-button-class)}
                ;;     [:> DragIcon {:class "flex-shrink-0"}]
                    [:div {:class "px-2 w-full"}
                     [:div {:class "text-base"} (:name point)]
                     [:div {:class "text-sm text-neutral-300"} (:location point)]]
                    [:div {:class "flex-shrink-0"}
                     [:button {:class "p-1"} [:> ChevronUpIcon]]
                     [:button {:class "p-1"} [:> ChevronDownIcon]]]]))]]

        [button {:label "Submit" :class "my-4"}]
        (doall (for [anom @!anoms]
                 [:span {:key (:reason anom)
                         :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                  (tr-error anom)]))]])))
