(ns ui.components.forms.route
  (:require ["@apollo/client" :refer (gql)]
            [uuid :rename {v4 uuid}]
            [react]
            [react-feather
             :rename {Menu MenuIcon
                      X XIcon}]
            [framer-motion :refer (Reorder)]
            [reagent.core :as r]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (to-datetime-local from-datetime-local)]
            [ui.lib.apollo :refer (parse-anoms use-query use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.lib.google.maps.directions :refer (calc-route)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.vector :refer (vec-remove)]
            [ui.utils.input :refer (debounce)]
            [ui.components.inputs.generic.combobox :refer (combobox)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.button :refer (button base-button-class)]))



(def FETCH_USER (gql (inline "queries/user/fetch.graphql")))
(def CREATE_ROUTE (gql (inline "mutations/route/create.graphql")))

(defn route-form []
  (let [!anoms (r/atom {})
        !state (r/atom {:seatId nil
                        :startAt (from-datetime-local (js/Date.))
                        ;; tuples of [draggable-item-id address-id] to use for reorderable list 
                        :address-tuples []
                        ;; directions api payload
                        :route nil})
        !loading-route (r/atom false)
        debounced-calc-route (debounce
                              (fn [w]
                                (if (seq w)
                                  (-> (calc-route w)
                                      (.then #(do (swap! !state assoc :route %)
                                                  (reset! !loading-route false))))
                                  (do
                                    (swap! !state assoc :route nil)
                                    (reset! !loading-route false))))
                              500)]
    (fn []
      (let [{:keys [data loading]} (use-query FETCH_USER {})
            [create] (use-mutation CREATE_ROUTE {})
            navigate (use-navigate)
            seats (some-> data :user :seats)
            addresses (some-> data :user :addresses)
            address-map (into {} (for [address addresses]
                                   {(:id address) address}))
            {:keys [seatId startAt address-tuples route]} @!state
            address-ids (mapv second address-tuples)
            selected-addresses (mapv #(get address-map %) address-ids)
            draggable-item-ids (mapv first address-tuples)
            draggable-item-map (into {} (for [[item-id address-id] (:address-tuples @!state)]
                                          {item-id address-id}))
            markers (->> route :legs (mapv
                                      (fn [{:keys [location address]}]
                                        {:position (select-keys location [:lat :lng])
                                         :title address})))]

        (react/useEffect
         (fn []
           (reset! !loading-route true)
           (debounced-calc-route selected-addresses)
           #())
         #js[address-tuples])

        (react/useEffect
         (fn []
           (dispatch [:map/set-paths (when route [(:path route)])])
           (dispatch [:map/set-points markers])
           #(do
              (dispatch [:map/set-paths nil])
              (dispatch [:map/set-points nil])))
         #js[route])

        [:<>
         (if loading
           [:p {:class "sr-only"} "Loading..."]
           [:p {:class "sr-only"} "Loaded..."])
         (if @!loading-route
           [:p {:class "sr-only"} "Loading route"]
           [:p {:class "sr-only"} "Loaded route"])
         [:form {:class "flex flex-col"
                 :on-submit
                 (fn [e]
                   (.preventDefault e)
                   (-> (create {:variables {:seatId seatId
                                            :startAt startAt
                                            :addressIds address-ids
                                            :route route}})
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
                      :on-change #(swap! !state update :address-tuples conj [(uuid) %])}]
           [:> (. Reorder -Group)
            {:axis "y"
             :values draggable-item-ids
             :on-reorder #(swap! !state assoc :address-tuples (mapv
                                                               (fn [id]
                                                                 [id (get draggable-item-map id)])
                                                               %))}
            (for [[idx [draggable-item-id address-id]] (map-indexed vector address-tuples)]
              (let [{:keys [name description]} (get address-map address-id)]
                ^{:key draggable-item-id}
                [:> (. Reorder -Item)
                 {:value draggable-item-id
                  :class (class-names
                          base-button-class
                          "cursor-grab flex items-center mb-2 rounded p-2")}
                 [:> MenuIcon {:class "flex-shrink-0"}]
                 [:div {:class "px-3 w-full"}
                  [:div {:class "text-base"} name]
                  [:div {:class "text-sm text-neutral-300"} description]]
                 [:button
                  {:type "button"
                   :class "flex-shrink-0"
                   :on-click #(swap! !state update :address-tuples vec-remove idx)}
                  [:> XIcon]]]))]]

          [button {:label "Submit" :class "my-4"}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
