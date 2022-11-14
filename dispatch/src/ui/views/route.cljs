(ns ui.views.route
  (:require ["@apollo/client" :refer (gql useQuery)]
            [react-feather
             :rename {ChevronUp ChevronUpIcon
                      ChevronDown ChevronDownIcon
                      X XIcon}]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->clj)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.vector :refer (vec-remove vec-move)]
            [ui.components.inputs.generic.combobox :refer (combobox)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.button :refer (button base-button-class)]))

(defn get-datetime-local [date]
  (.setMinutes date (- (.getMinutes date) (.getTimezoneOffset date)))
  (.slice (.toISOString date) 0 16))

(def FETCH_USER (gql (inline "queries/user/fetch.graphql")))

(defn view []
  (let [!anoms (r/atom {})
        !state (r/atom {:seat-id nil
                        :start-at (get-datetime-local (js/Date.))
                        :stop-ids []})]
    (fn []
      (let [query (useQuery FETCH_USER)
            {:keys [data]} (->clj query)
            seats (some-> data :user :seats)
            addresses (some-> data :user :addresses)
            stops (mapv (fn [id]
                          (some #(when (= id (:id %)) %) addresses))
                        (-> @!state :stop-ids))]
        [:div {:class (class-names padding)}
         [:form {:class "flex flex-col"
                 :on-submit
                 (fn [e]
                   (.preventDefault e)
                  ;;  (-> (login (->js {:variables @!state}))
                  ;;      (.then (fn [res]
                  ;;               (create-session (-> res ->clj :data :loginUser))
                  ;;               (.. result -client resetStore)
                  ;;               (navigate "/fleet")))
                  ;;      (.catch #(reset! !anoms (parse-anoms %))))
                   )}
          [combobox {:label "Assigned seat"
                     :class "pb-4"
                     :value (:seat-id @!state)
                     :options seats
                     :option-to-label #(:name %)
                     :option-to-value #(:id %)
                     :on-change #(swap! !state assoc :seat-id %)}]
          [input {:id "start"
                  :label "Departure time"
                  :placeholder "Select date and time"
                  :type "datetime-local"
                  :value (:start-at @!state)
                  :required true
                  :class "pb-4"
                  :on-text #(swap! !state assoc :start-at %)}]
          [:label {:class "mb-2 text-sm"} "Manage stops"]
          [:div
           [combobox {:aria-label "Add address"
                      :placeholder "Search for addresses"
                      :class "mb-4"
                      :options addresses
                      :option-to-label #(:name %)
                      :option-to-value #(:id %)
                      :on-change #(swap! !state update :stop-ids conj %)}]
           [:ol
            (doall (for [[idx {:keys [id name lat lng]}] (map-indexed vector stops)]
                     (let [first? (= idx 0)
                           last? (= (+ idx 1) (count stops))
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
                            :on-click #(swap! !state update :stop-ids vec-move idx (- idx 1))}
                           [:> ChevronUpIcon]]
                          [:button
                           {:type "button"
                            :disabled last?
                            :class (when last? disabled-class)
                            :on-click #(swap! !state update :stop-ids vec-move idx (+ idx 1))}
                           [:> ChevronDownIcon]]]
                         [:button
                          {:type "button"
                           :class "ml-2"
                           :on-click #(swap! !state update :stop-ids vec-remove idx)}
                          [:> XIcon]]]])))]]

          [button {:label "Submit" :class "my-4"}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
