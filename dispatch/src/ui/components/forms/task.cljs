(ns ui.components.forms.task
  (:require ["uuid" :rename {v4 uuid}]
            ["react" :refer (useEffect useState)]
            ["react-feather" :rename {Menu MenuIcon
                                      X XIcon}]
            ["framer-motion" :refer (Reorder)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (to-datetime-local)]
            [ui.lib.apollo :refer (gql parse-anoms use-query use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.lib.google.maps.directions :refer (calc-route)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.vector :refer (vec-remove)]
            [ui.utils.input :refer (debounce-cb)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.button :refer (base-button-class)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def FETCH_ORGANIZATION_TASK_OPTIONS (gql (inline "queries/user/organization/fetch-task-options.graphql")))
(def CREATE_TASK (gql (inline "mutations/task/create-task.graphql")))

(defn task-form [{:keys [id]}]
  (let [navigate (use-navigate)
        query (use-query FETCH_ORGANIZATION_TASK_OPTIONS {:variables {:taskId id}})
        [create status] (use-mutation CREATE_TASK {})
        [anoms set-anoms] (useState {})
        [state set-state] (useState {:agentId nil
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
        [loading-route set-loading-route] (useState false)
        loading (or
                 (:loading query)
                 (:loading status)
                 loading-route)
        {:keys [agents places task]} (some-> query :data :user :organization)
        place-map (into {} (for [place places]
                             {(:id place) place}))
        {:keys [agentId startAt origin-id destination-id stop-tuples route]} state
        stop-ids (mapv second stop-tuples)
        place-ids (filterv some? (concat [origin-id] stop-ids [destination-id]))
        selected-places (mapv #(get place-map %) place-ids)
        draggable-item-ids (mapv first stop-tuples)
        draggable-item-map (into {} (for [[item-id place-id] stop-tuples]
                                      {item-id place-id}))]
    (useEffect
     (fn []
       (prn "hello")
       (when (and (not (:loading query)) task)
         (set-state {:agentId (-> task :agent :id)
                     :startAt (-> task :startAt)
                     :origin-id (-> task :stops first :place :id)
                     :destination-id (-> task :stops first :place :id)
                     :stop-tuples (->>
                                   task
                                   :stops
                                   (drop 1)
                                   (drop-last 1)
                                   (mapv (fn [{:keys [place]}]
                                           [(uuid) (:id place)])))}))
       #())
     #js[(:loading query)])

    (useEffect
     (fn []
       (set-loading-route true)
       (debounce-cb
        (fn []
          (if (seq selected-places)
            (-> (calc-route selected-places)
                (.then #(do (set-state (fn [s]
                                         (assoc s :route %)))
                            (set-loading-route false))))
            (do
              (set-state (fn [s]
                           (assoc s :route nil)))
              (set-loading-route false))))
        500)
       #())
     #js[origin-id destination-id stop-tuples])

    (use-map-items
     loading
     {:tasks (when route [{:route route}])
      :places selected-places}
     [loading route selected-places])

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
                  (.catch #(set-anoms (parse-anoms %)))))}
     [combobox {:label (tr [:field/agent])
                :value (:agentId state)
                :required true
                :class "mb-4"
                :options agents
                :option-to-label #(:name %)
                :option-to-value #(:id %)
                :on-change #(set-state (fn [s] (assoc s :agentId %)))}]
     [input {:label (tr [:field/departure])
             :type "datetime-local"
             :value (to-datetime-local
                     (js/Date. (:startAt state)))
             :required true
             :class "mb-4"
             :on-text #(set-state (fn [s] (assoc s :startAt (js/Date. %))))}]
     [combobox {:label (tr [:field/origin])
                :value (:origin-id state)
                :required true
                :class "mb-4"
                :options places
                :option-to-label #(:name %)
                :option-to-value #(:id %)
                :on-change (fn [id]
                             (set-state (fn [s] (assoc s :origin-id id)))
                             (when-not (:destination-id state)
                               (set-state (fn [s] (assoc s :destination-id id)))))}]
     (when (and (:origin-id state) (:destination-id state))
       [:div {:class "mb-4"}
        [:label {:class "block mb-2 text-sm"} (tr [:field/stops])]
        [:div
         [:> (. Reorder -Group)
          {:axis "y"
           :values draggable-item-ids
           :on-reorder #(set-state
                         (fn [s] (assoc s :stop-tuples
                                        (mapv (fn [id] [id (get draggable-item-map id)]) %))))}
          (for [[idx [draggable-item-id place-id]] (map-indexed vector stop-tuples)]
            (let [{:keys [name description]} (get place-map place-id)]
              ^{:key draggable-item-id}
              [:> (. Reorder -Item)
               {:value draggable-item-id
                :class (class-names
                        base-button-class
                        "cursor-grab flex items-center mb-2 rounded p-2")}
               [:> MenuIcon {:class "flex-shrink-0"}]
               [:div {:class "px-3 w-full min-w-0"}
                [:div {:class "text-sm truncate"} name]
                [:div {:class "text-xs text-neutral-300 truncate"} description]]
               [:button
                {:type "button"
                 :class "flex-shrink-0"
                 :on-click #(set-state (fn [s] (update s :stop-tuples vec-remove idx)))}
                [:> XIcon]]]))]
         [combobox {:aria-label (tr [:field/add-stop])
                    :placeholder (tr [:field/add-stop])
                    :options places
                    :option-to-label #(:name %)
                    :option-to-value #(:id %)
                    :on-change #(set-state (fn [s]  (update s :stop-tuples conj [(uuid) %])))}]]])
     [combobox {:label (tr [:field/destination])
                :value (:destination-id state)
                :required true
                :class "mb-4"
                :options places
                :option-to-label #(:name %)
                :option-to-value #(:id %)
                :on-change #(set-state (fn [s] (assoc s :destination-id %)))}]
     [submit-button {:loading loading}]
     [errors anoms]]))
