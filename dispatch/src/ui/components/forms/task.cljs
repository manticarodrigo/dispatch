(ns ui.components.forms.task
  (:require ["uuid" :rename {v4 uuid}]
            ["react" :refer (useEffect)]
            ["react-feather" :rename {Menu MenuIcon
                                      X XIcon}]
            ["framer-motion" :refer (Reorder)]
            [reagent.core :as r]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (to-datetime-local)]
            [ui.lib.apollo :refer (gql parse-anoms use-query use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.lib.google.maps.directions :refer (calc-route)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.vector :refer (vec-remove)]
            [ui.utils.input :refer (debounce)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.button :refer (base-button-class)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))



(def FETCH_ORGANIZATION_TASK_OPTIONS (gql (inline "queries/user/organization/fetch-task-options.graphql")))
(def CREATE_TASK (gql (inline "mutations/task/create-task.graphql")))

(defn task-form []
  (let [!anoms (r/atom {})
        !state (r/atom {:agentId nil
                        :startAt (d/set
                                  (js/Date.)
                                  #js{:hours 8
                                      :minutes 0
                                      :seconds 0
                                      :milliseconds 0})
                        :origin-id nil
                        :destination-id nil
                        ;; tuples of [draggable-item-id place-id] to use for reorderable list 
                        :stop-tuples []
                        ;; directions api payload
                        :route nil})
        !loading-route (r/atom false)
        -calc-route (debounce
                     (fn [places]
                       (if (seq places)
                         (-> (calc-route places)
                             (.then #(do (swap! !state assoc :route %)
                                         (reset! !loading-route false))))
                         (do
                           (swap! !state assoc :route nil)
                           (reset! !loading-route false))))
                     500)]
    (fn []
      (let [navigate (use-navigate)
            query (use-query FETCH_ORGANIZATION_TASK_OPTIONS {})
            [create status] (use-mutation CREATE_TASK {})
            loading (or
                     (:loading query)
                     (:loading status)
                     @!loading-route)
            {:keys [agents places]} (some-> query :data :user :organization)
            place-map (into {} (for [place places]
                                 {(:id place) place}))
            {:keys [agentId startAt origin-id destination-id stop-tuples route]} @!state
            stop-ids (mapv second stop-tuples)
            place-ids (filterv some? (concat [origin-id] stop-ids [destination-id]))
            selected-places (mapv #(get place-map %) place-ids)
            draggable-item-ids (mapv first stop-tuples)
            draggable-item-map (into {} (for [[item-id place-id] stop-tuples]
                                          {item-id place-id}))
            markers (->> route
                         :legs
                         (map-indexed
                          (fn [idx {:keys [location]}]
                            {:title (-> selected-places (get idx) :name)
                             :position (select-keys location [:lat :lng])}))
                         vec)]

        (useEffect
         (fn []
           (reset! !loading-route true)
           (-calc-route selected-places)
           #())
         #js[origin-id destination-id stop-tuples])

        (useEffect
         (fn []
           (dispatch [:map {:paths (when route [(:path route)])
                            :points markers}])
           #())
         #js[route])

        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (create {:variables
                               {:agentId agentId
                                :startAt startAt
                                :placeIds place-ids
                                :route route}})
                      (.then #(navigate "/organization/tasks"))
                      (.catch #(reset! !anoms (parse-anoms %)))))}
         [combobox {:label (tr [:field/agent])
                    :value (:agentId @!state)
                    :required true
                    :class "mb-4"
                    :options agents
                    :option-to-label #(:name %)
                    :option-to-value #(:id %)
                    :on-change #(swap! !state assoc :agentId %)}]
         [input {:label (tr [:field/departure])
                 :type "datetime-local"
                 :value (to-datetime-local
                         (js/Date. (:startAt @!state)))
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :startAt (js/Date. %))}]
         [combobox {:label (tr [:field/origin])
                    :value (:origin-id @!state)
                    :required true
                    :class "mb-4"
                    :options places
                    :option-to-label #(:name %)
                    :option-to-value #(:id %)
                    :on-change (fn [id]
                                 (swap! !state assoc :origin-id id)
                                 (when-not (:destination-id @!state)
                                   (swap! !state assoc :destination-id id)))}]
         (when (and (:origin-id @!state) (:destination-id @!state))
           [:div {:class "mb-4"}
            [:label {:class "block mb-2 text-sm"} (tr [:field/stops])]
            [:div
             [:> (. Reorder -Group)
              {:axis "y"
               :values draggable-item-ids
               :on-reorder #(swap!
                             !state
                             assoc
                             :stop-tuples
                             (mapv (fn [id] [id (get draggable-item-map id)]) %))}
              (for [[idx [draggable-item-id place-id]] (map-indexed vector stop-tuples)]
                (let [{:keys [name description]} (get place-map place-id)]
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
             [combobox {:aria-label (tr [:field/add-stop])
                        :placeholder (tr [:field/add-stop])
                        :options places
                        :option-to-label #(:name %)
                        :option-to-value #(:id %)
                        :on-change #(swap! !state update :stop-tuples conj [(uuid) %])}]]])
         [combobox {:label (tr [:field/destination])
                    :value (:destination-id @!state)
                    :required true
                    :class "mb-4"
                    :options places
                    :option-to-label #(:name %)
                    :option-to-value #(:id %)
                    :on-change #(swap! !state assoc :destination-id %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
