(ns ui.components.forms.route
  (:require ["@apollo/client" :refer (gql useQuery useMutation)]
            [react :as react]
            [react-feather
             :rename {ChevronUp ChevronUpIcon
                      ChevronDown ChevronDownIcon
                      X XIcon}]
            [reagent.core :as r]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [common.utils.date :refer (to-datetime-local from-datetime-local)]
            [ui.lib.apollo :refer (parse-anoms)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.vector :refer (vec-remove vec-move)]
            [ui.components.inputs.generic.combobox :refer (combobox)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.button :refer (button base-button-class)]))



(def FETCH_USER (gql (inline "queries/user/fetch.graphql")))
(def CREATE_ROUTE (gql (inline "mutations/route/create.graphql")))

(defn route-form []
  (let [!anoms (r/atom {})
        !state (r/atom {:seatId nil
                        :startAt (from-datetime-local (js/Date.))
                        :addressIds []})]
    (fn []
      (let [query (useQuery FETCH_USER)
            [create] (useMutation CREATE_ROUTE)
            navigate (use-navigate)
            {:keys [data loading]} (->clj query)
            seats (some-> data :user :seats)
            addresses (some-> data :user :addresses)
            selected-addresses (mapv (fn [id]
                                       (some #(when (= id (:id %)) %) addresses))
                                     (-> @!state :addressIds))]

        (react/useEffect
         (fn []
           (dispatch [:stops/set selected-addresses])
           #())
         #js[selected-addresses])

        [:<>
         (if loading
           [:p {:class "sr-only"} "Loading..."]
           [:p {:class "sr-only"} "Loaded..."])
         [:form {:class "flex flex-col"
                 :on-submit
                 (fn [e]
                   (.preventDefault e)
                   (-> (create (->js {:variables @!state}))
                       (.then #(navigate "/seats"))
                       (.catch #(reset! !anoms (parse-anoms %)))))}
          [combobox {:label "Assigned seat"
                     :class "pb-4"
                     :value (:seatId @!state)
                     :options seats
                     :option-to-label #(:name %)
                     :option-to-value #(:id %)
                     :on-change #(swap! !state assoc :seatId %)}]
          [input {:id "start"
                  :label "Departure time"
                  :placeholder "Select date and time"
                  :type "datetime-local"
                  :value (to-datetime-local
                          (js/Date. (:startAt @!state)))
                  :required true
                  :class "pb-4"
                  :on-text #(swap! !state assoc :startAt
                                   (from-datetime-local (js/Date. %)))}]
          [:label {:class "mb-2 text-sm"} "Manage stops"]
          [:div
           [combobox {:aria-label "Add address"
                      :placeholder "Search for addresses"
                      :class "mb-4"
                      :options addresses
                      :option-to-label #(:name %)
                      :option-to-value #(:id %)
                      :on-change #(swap! !state update :addressIds conj %)}]
           [:ol
            (for [[idx {:keys [id name lat lng]}] (map-indexed vector selected-addresses)] 
              (let [first? (= idx 0)
                    last? (= (+ idx 1) (count selected-addresses))
                    disabled-class "cursor-not-allowed text-neutral-500"]
                ^{:key (str idx "-" id)}
                [:li {:class (class-names
                              base-button-class
                              "flex items-center mb-2 rounded p-2 bg-neutral-900")}
                 [:div {:class "flex-shrink-0 flex justify-center items-center rounded-full w-8 h-8 bg-neutral-600"}
                  (+ idx 1)]
                 [:div {:class "px-2 w-full"}
                  [:div {:class "text-base"} name]
                  [:div {:class "text-sm text-neutral-300"} lat ", " lng]]
                 [:div {:class "flex-shrink-0 flex"}
                  [:div {:class "flex-shrink-0 flex flex-col"}
                   [:button
                    {:type "button"
                     :disabled first?
                     :class (when first? disabled-class)
                     :on-click #(swap! !state update :addressIds vec-move idx (- idx 1))}
                    [:> ChevronUpIcon]]
                   [:button
                    {:type "button"
                     :disabled last?
                     :class (when last? disabled-class)
                     :on-click #(swap! !state update :addressIds vec-move idx (+ idx 1))}
                    [:> ChevronDownIcon]]]
                  [:button
                   {:type "button"
                    :class "ml-2"
                    :on-click #(swap! !state update :addressIds vec-remove idx)}
                   [:> XIcon]]]]))]]

          [button {:label "Submit" :class "my-4"}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
