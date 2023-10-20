(ns ui.components.forms.task
  (:require ["react" :refer (useEffect useState)]
            ["react-feather" :rename {Menu DragIcon}]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (to-datetime-local)]
            [ui.lib.apollo :refer (gql parse-anoms use-query use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.lib.google.maps.directions :refer (calc-route)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.input :refer (debounce-cb)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.hooks.use-click-near-origin :refer (use-click-near-origin)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.reorder :refer (reorder)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.parts.stop :refer (stop-order stop-transition stop-details)]
            [ui.components.errors :refer (errors)]))

(def FETCH_ORGANIZATION_TASK_OPTIONS (gql (inline "queries/user/organization/fetch-task-options.graphql")))
(def CREATE_TASK (gql (inline "mutations/task/create-task.graphql")))

(defn task-form [{:keys [id]}]
  (let [navigate (use-navigate)
        query (use-query FETCH_ORGANIZATION_TASK_OPTIONS (if id {:variables {:taskId id}} {}))
        [create status] (use-mutation CREATE_TASK {})
        [anoms set-anoms] (useState {})
        [state set-state] (useState {:agentId nil
                                     :startAt (d/set
                                               (js/Date.)
                                               #js{:hours 8
                                                   :minutes 0
                                                   :seconds 0
                                                   :milliseconds 0})
                        ;; tuples of [draggable-item-id stop] to use for reorderable list 
                                     :stop-tuples []
                        ;; directions api payload
                                     :route nil})
        [open-stop-index set-open-stop-index] (useState nil)
        [loading-route set-loading-route] (useState false)
        loading (or
                 (:loading query)
                 (:loading status)
                 loading-route)
        {:keys [agentId startAt stop-tuples route]} state
        {:keys [agents places task shipments]} (some-> query :data :user :organization)
        selected-stop (when open-stop-index (-> stop-tuples (nth open-stop-index) second))
        shipments (if (:shipment selected-stop)
                    (conj shipments (:shipment selected-stop))
                    shipments)
        place-map (into {} (for [place places]
                             {(:id place) place}))
        shipment-map (into {} (for [shipment shipments]
                                {(:id shipment) shipment}))
        place-ids (mapv #(-> % second :place :id) stop-tuples)
        selected-places (mapv #(get place-map %) place-ids)
        durations (some->> route :legs (map-indexed
                                        (fn [idx leg]
                                          (let [travel-duration (:duration leg)
                                                stop-duration (some-> (get stop-tuples (dec idx)) second :shipment :duration)]
                                            (+ travel-duration stop-duration)))))
        origin-click (use-click-near-origin)]

    (useEffect
     (fn []
       (when (and (not (:loading query)) task)
         (set-state {:agentId (-> task :agent :id)
                     :startAt (-> task :startAt)
                     :stop-tuples (->>
                                   task
                                   :stops
                                   (map-indexed vector)
                                   vec)}))
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
     #js[stop-tuples])

    (use-map-items
     loading
     {:tasks (when route [{:route route}])
      :places selected-places}
     [loading route selected-places])

    [:form {:class "flex flex-col overflow-auto"
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
     [:div {:class "p-4"}
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
              :on-text #(set-state (fn [s] (assoc s :startAt (js/Date. %))))}]]
     [:div
      ;; {:class "mb-4"}
      [:label {:class "block mb-2 pt-4 px-4 text-sm"} (tr [:field/stops]) [:> DragIcon {:class "inline ml-2 w-4 h-4 text-neutral-300"}]]
      [:div
       [modal
        {:show (some? open-stop-index)
         :title "Edit stop"
         :on-close #(set-open-stop-index nil)}
        [:div {:class "p-4"}
         [combobox {:label (tr [:field/place])
                    :value (-> selected-stop :place :id)
                    :class "mb-4"
                    :options places
                    :option-to-label #(:name %)
                    :option-to-value :id
                    :on-change #(set-state (fn [s]
                                             (assoc-in s
                                                       [:stop-tuples open-stop-index 1 :place]
                                                       (get place-map %))))}]
         [combobox {:label (tr [:field/shipment "Shipment"])
                    :value (-> selected-stop :shipment :id)
                    :class "mb-4"
                    :options shipments
                    :option-to-label #(-> % :place :name)
                    :option-to-value :id
                    :on-change #(set-state (fn [s]
                                             (assoc-in s
                                                       [:stop-tuples open-stop-index 1 :shipment]
                                                       (get shipment-map %))))}]
         [input {:label (tr [:field/duration "Duration"])
                 :value (or (-> selected-stop :duration)
                            (-> selected-stop :shipment :duration))
                 :class "mb-4"
                 :on-text #(set-state (fn [s]
                                        (assoc-in s
                                                  [:stop-tuples open-stop-index 1 :duration]
                                                  (js/parseInt %))))}]]]
       [reorder
        {:tuples stop-tuples
         :class "relative divide-y divide-neutral-800 w-full"
         :render-item (fn [idx stop]
                        (let [{:keys [place shipment finishedAt]} stop
                              {:keys [name description]} place
                              {:keys [legs]} route
                              {:keys [duration distance]} (when (< idx (count legs)) (nth legs idx))
                              {:keys [weight volume]
                               shipment-duration :duration} shipment
                              start-at (some->> durations (take idx) (reduce + 960) (d/addSeconds startAt))
                              end-at (when (and start-at shipment-duration) (d/addSeconds start-at shipment-duration))]
                          [:div {:class "relative bg-neutral-900 hover:brightness-110 transition"
                                 :on-mouse-down (:on-mouse-down origin-click)
                                 :on-click ((:get-on-click origin-click) #(set-open-stop-index idx))}
                           (when (> idx 0)
                             [:div {:class "absolute bottom-full translate-y-1/2 w-full text-center"}
                              [stop-transition
                               {;;  :break break
                                :duration duration
                                :distance distance}]])
                           [:div {:class "flex py-6 px-4"}
                            [stop-order idx]
                            [:div {:class "pr-2 pl-4 w-full min-w-0"}
                             [stop-details
                              {;; :visits visits
                               :weight weight
                               :volume volume
                               :finished-at finishedAt
                               :start-at start-at
                               :end-at end-at}]
                             [:div {:class "mb-2 text-sm"} name]
                             [:div {:class "mb-2 text-xs text-neutral-300"} description]]]]))
         :on-reorder #(set-state (fn [s] (assoc s :stop-tuples %)))}]
       [:div {:class "p-4"}
        [combobox {:aria-label (tr [:field/add-stop])
                   :placeholder (tr [:field/add-stop])
                   :options places
                   :option-to-label #(:name %)
                   :option-to-value #(:id %)
                   :class "mt-4"
                   :on-change #(set-state (fn [s]  (update s :stop-tuples conj [(count stop-tuples) {:place (get place-map %)}])))}]]]]
     [:div {:class "p-4"}
      [submit-button {:loading loading}]]
     [errors anoms]]))
