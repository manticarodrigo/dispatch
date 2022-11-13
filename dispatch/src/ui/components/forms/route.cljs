(ns ui.components.forms.route
  (:require ["@apollo/client" :refer (gql useQuery)]
            [react-feather
             :rename {ArrowUp ChevronUpIcon
                      ArrowDown ChevronDownIcon}]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->clj)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.generic.combobox :refer (combobox)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.button :refer (button base-button-class)]))

(defn get-datetime-local [date]
  (.setMinutes date (- (.getMinutes date) (.getTimezoneOffset date)))
  (.slice (.toISOString date) 0 16))

(def FETCH_USER (gql (inline "queries/user/fetch.graphql")))

(defn route-form [{initial-state :initial-state on-submit :on-submit}]
  (let [!state (r/atom (or initial-state {:start (get-datetime-local (js/Date.))
                                          :addresses [{:name "Subway" :location "Galerias"}
                                                      {:name "Pizza Hut" :location "Galerias"}
                                                      {:name "La Colonia" :location "Galerias"}]}))
        !anoms (r/atom {})]

    (fn []
      (let [query (useQuery FETCH_USER)
            {:keys [data]} (->clj query)]
        [:<>
         [:form {:class "flex flex-col"
                 :on-submit on-submit}
          [combobox {:label "Assigned seat"
                     :class "pb-4"
                     :options (some-> data :user :seats)
                     :option-to-label #(:name %)
                     :option-to-value #(:id %)
                     :on-change (fn [])}]
          [input {:id "origin"
                  :label "Origin address"
                  :value (:username @!state)
                  :required true
                  :class "pb-4"
                  :on-text #(swap! !state assoc :origin %)}]
          [input {:id "start"
                  :label "Departure time"
                  :placeholder "Select date and time"
                  :type "datetime-local"
                  :value (:start @!state)
                  :required true
                  :class "pb-4"
                  :on-text #(swap! !state assoc :start %)}]
          [:label {:class "mb-2 text-sm"} "Manage stops"]
          [:div
           [combobox {:aria-label "Add address"
                      :placeholder "Search for addresses"
                      :class "mb-4"
                      :options (some-> data :user :addresses)
                      :option-to-label #(:name %)
                      :option-to-value #(:id %)
                      :on-change (fn [])}]
           [:ol
            (doall (for [[idx point] (map-indexed vector (-> @!state :addresses))]
                     ^{:key idx}
                     [:li {:class (class-names "flex items-center mb-2 rounded p-2 bg-neutral-900" base-button-class)}
                      [:div {:class "w-full"}
                       [:div {:class "text-base"} (:name point)]
                       [:div {:class "text-sm text-neutral-300"} (:location point)]]
                      [:div {:class "flex-shrink-0"}
                       [:button {:type "button" :class "pl-2"} [:> ChevronUpIcon]]
                       [:button {:type "button" :class "pl-2"} [:> ChevronDownIcon]]]]))]]

          [button {:label "Submit" :class "my-4"}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
