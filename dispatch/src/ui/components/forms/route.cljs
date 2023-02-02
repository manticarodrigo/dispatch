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
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.button :refer (button base-button-class)]))



(def FETCH_USER (gql (inline "queries/user/fetch.graphql")))
(def CREATE_ROUTE (gql (inline "mutations/route/create.graphql")))

(defn route-form []
  (let [!anoms (r/atom {})
        !state (r/atom {:seatId nil
                        :startAt (from-datetime-local (js/Date.))
                        :origin-id nil
                        :destination-id nil
                        ;; tuples of [draggable-item-id address-id] to use for reorderable list 
                        :stop-tuples []
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
      (let [navigate (use-navigate)
            query (use-query FETCH_USER {})
            [create status] (use-mutation CREATE_ROUTE {})
            loading (or
                     (:loading query)
                     (:loading status)
                     @!loading-route)
            {:keys [seats addresses]} (some-> query :data :user)
            address-map (into {} (for [address addresses]
                                   {(:id address) address}))
            {:keys [seatId startAt origin-id destination-id stop-tuples route]} @!state
            stop-ids (mapv second stop-tuples)
            address-ids (filterv some? (concat [origin-id] stop-ids [destination-id]))
            selected-addresses (mapv #(get address-map %) address-ids)
            draggable-item-ids (mapv first stop-tuples)
            draggable-item-map (into {} (for [[item-id address-id] (:stop-tuples @!state)]
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
         #js[origin-id destination-id stop-tuples])

        (react/useEffect
         (fn []
           (dispatch [:map/set-paths (when route [(:path route)])])
           (dispatch [:map/set-points markers])
           #(do
              (dispatch [:map/set-paths nil])
              (dispatch [:map/set-points nil])))
         #js[route])

        [:<>
         [:form {:class "flex flex-col"
                 :on-submit
                 (fn [e]
                   (.preventDefault e)
                   (-> (create {:variables {:seatId seatId
                                            :startAt startAt
                                            :addressIds address-ids
                                            :route route}})
                       (.then #(navigate "/admin/routes"))
                       (.catch #(reset! !anoms (parse-anoms %)))))}
          [combobox {:label "Assigned seat"
                     :value (:seatId @!state)
                     :required true
                     :class "mb-4"
                     :options seats
                     :option-to-label #(:name %)
                     :option-to-value #(:id %)
                     :on-change #(swap! !state assoc :seatId %)}]
          [input {:id "start"
                  :label "Departure time"
                  :type "datetime-local"
                  :value (to-datetime-local
                          (js/Date. (:startAt @!state)))
                  :required true
                  :class "mb-4"
                  :on-text #(swap! !state assoc :startAt
                                   (from-datetime-local (js/Date. %)))}]
          [combobox {:label "Origin address"
                     :value (:origin-id @!state)
                     :required true
                     :class "mb-4"
                     :options addresses
                     :option-to-label #(:name %)
                     :option-to-value #(:id %)
                     :on-change (fn [id]
                                  (swap! !state assoc :origin-id id)
                                  (when-not (:destination-id @!state)
                                    (swap! !state assoc :destination-id id)))}]
          (when (and (:origin-id @!state) (:destination-id @!state))
            [:div {:class "mb-4"}
             [:label {:class "block mb-2 text-sm"} "Manage stops"]
             [:div
              [:> (. Reorder -Group)
               {:axis "y"
                :values draggable-item-ids
                :on-reorder #(swap!
                              !state
                              assoc
                              :stop-tuples
                              (mapv (fn [id] [id (get draggable-item-map id)]) %))}
               (for [[idx [draggable-item-id address-id]] (map-indexed vector stop-tuples)]
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
                      :on-click #(swap! !state update :stop-tuples vec-remove idx)}
                     [:> XIcon]]]))]
              [combobox {:aria-label "Add new address"
                         :placeholder "Add new address"
                         :options addresses
                         :option-to-label #(:name %)
                         :option-to-value #(:id %)
                         :on-change #(swap! !state update :stop-tuples conj [(uuid) %])}]]])
          [combobox {:label "Destination address"
                     :value (:destination-id @!state)
                     :required true
                     :class "mb-4"
                     :options addresses
                     :option-to-label #(:name %)
                     :option-to-value #(:id %)
                     :on-change #(swap! !state assoc :destination-id %)}]
          [button
           {:label (if loading
                     [:span {:class "flex justify-center items-center"}
                      [spinner {:class "mr-2 w-5 h-5"}] "Loading..."]
                     "Create route")
            :class (class-names "my-4 w-full" (when loading "cursor-progress"))
            :disabled loading}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
