(ns ui.views.organization.plan.detail
  (:require [react :refer (useState useEffect)]
            [reagent.core :as r]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.header :refer (header)]
            [ui.components.tables.plan :refer (plan-table)]
            [ui.components.tables.shipment :refer (shipment-table)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.loading-button :refer (loading-button)]
            [ui.components.modal :refer (modal)]))

(def FETCH_PLAN (gql (inline "queries/user/organization/fetch-plan.graphql")))
(def OPTIMIZE_PLAN (gql (inline "mutations/plan/optimize.graphql")))
(def CREATE_PLAN_TASKS (gql (inline "mutations/plan/create-plan-tasks.graphql")))

(defn details [{:keys [result]}]
  (let [{:keys [skipped]} result
        [modal-open? set-modal-open?] (useState false)]
    [:div {:class "border-b border-neutral-700 p-4"}
     [:p {:class "text-sm"}
      [button {:label (str (tr [:table.plan/view-skipped-shipments]) " (" (count skipped) ")")
               :class "text-sm"
               :on-click #(set-modal-open? true)}]
      [modal {:show modal-open?
              :title "Skipped shipments"
              :on-close #(set-modal-open? false)}
       [shipment-table {:shipments skipped}]]]]))

(defn view []
  (let [!selected-agents (r/atom {})]
    (fn []
      (let [{plan-id :plan} (use-params)
            {:keys [data loading]} (use-query FETCH_PLAN {:variables {:planId plan-id}})
            [optimize optimize-status] (use-mutation OPTIMIZE_PLAN {})
            [create-tasks create-tasks-status] (use-mutation CREATE_PLAN_TASKS {})
            {:keys [plan agents]} (-> data :user :organization)
            {:keys [result startAt endAt vehicles shipments]} plan
            {:keys [routes]} result
            [selected-rows set-selected-rows] (useState #js{})
            selected-indexes (->> selected-rows js/Object.keys (map int))]

        (useEffect
         (fn []
          ;;  todo set selected agents from task relations
           (when (seq agents)
             (reset! !selected-agents
                     (into {}
                           (for [[idx {:keys [id]}]
                                 (map-indexed vector agents)]
                             {idx id}))))
           #())
         #js[data])

        [:main {:class "flex flex-col w-full h-full min-w-0 min-h-0"}
         [header {:title (if loading
                           (str (tr [:misc/loading]) "...")
                           (tr [:view.plan.detail/title] [startAt endAt]))
                  :actions [:<>
                            (when-not loading
                              [loading-button
                               {:loading (:loading optimize-status)
                                :label (tr [:verb/optimize])
                                :class "capitalize"
                                :on-click #(optimize {:variables {:planId plan-id}})}])
                            (when (seq selected-indexes)
                              [loading-button
                               {:loading (:loading create-tasks-status)
                                :label (tr [:verb/create])
                                :class "ml-4 capitalize"
                                :on-click
                                #(do
                                   (create-tasks
                                    {:variables
                                     {:input
                                      {:planId plan-id
                                       :assignments
                                       (map
                                        (fn [idx]
                                          {:agentId (get @!selected-agents (int idx))
                                           :vehicleId (-> (nth routes (int idx)) :vehicle :id)
                                           :visits (->> (nth routes (int idx)) :visits
                                                        (map (fn [visit]
                                                               (let [{:keys [depot shipment]} visit]
                                                                 (if depot
                                                                   {:placeId (:id depot)}
                                                                   {:placeId (-> shipment :place :id)
                                                                    :shipmentId (-> shipment :id)})))))})
                                        selected-indexes)}}})
                                   (set-selected-rows #js{}))}
                               (tr [:verb/create] (tr [:noun/tasks]))])]}]
         (if loading [:div {:class "p-4"}
                      (tr [:misc/loading]) "..."]
             (if result
               [:div {:class "flex flex-col w-full h-full min-w-0 min-h-0"}
                [details {:result result}]
                [:div {:class "overflow-auto w-full h-full min-w-0 min-h-0"}
                 [plan-table {:agents agents
                              :result routes
                              :selected-rows selected-rows
                              :set-selected-rows set-selected-rows
                              :!selected-agents !selected-agents}]]]
               [:div {:class "p-4"}
                [:div (count vehicles) " " (tr [:noun/vehicles])]
                [:div (count shipments) " " (tr [:noun/shipments])]
                [loading-button
                 {:loading (:loading optimize-status)
                  :label (tr [:verb/optimize])
                  :class "mt-4 capitalize"
                  :on-click #(optimize {:variables {:planId plan-id}})}]]))]))))
