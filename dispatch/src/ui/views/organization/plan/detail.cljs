(ns ui.views.organization.plan.detail
  (:require [react :refer (useState useEffect)]
            [reagent.core :as r]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.header :refer (header)]
            [ui.components.tables.plan :refer (plan-table)]
            [ui.components.inputs.button :refer (button)]))

(def FETCH_PLAN (gql (inline "queries/user/organization/fetch-plan.graphql")))
(def OPTIMIZE_PLAN (gql (inline "mutations/plan/optimize.graphql")))
(def CREATE_PLAN_TASKS (gql (inline "mutations/plan/create-plan-tasks.graphql")))

(defn view []
  (let [!selected-agents (r/atom {})]
    (fn []
      (let [{plan-id :plan} (use-params)
            {:keys [data loading]} (use-query FETCH_PLAN {:variables {:planId plan-id}})
            [optimize] (use-mutation OPTIMIZE_PLAN {})
            [create-tasks] (use-mutation CREATE_PLAN_TASKS {})
            {:keys [plan agents]} (-> data :user :organization)
            {:keys [result startAt endAt]} plan
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

        [:main {:class "flex flex-col w-full h-full"}
         [header {:title (if loading
                           (str (tr [:misc/loading]) "...")
                           (tr [:view.plan.detail/title] [startAt endAt]))
                  :actions (when (seq selected-indexes)
                             [button
                              {:label (tr [:verb/create])
                               :class "capitalize"
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
                                          :vehicleId (-> (nth result (int idx)) :vehicle :id)
                                          :shipmentIds (->> (nth result (int idx)) :visits (map :shipment) (map :id))})
                                       selected-indexes)}}})
                                  (set-selected-rows #js{}))}
                              (tr [:verb/create] (tr [:noun/tasks]))])}]
         (if loading (str (tr [:misc/loading]) "...")
             (if result
               [:div {:class "overflow-auto h-full"}
                [plan-table {:agents agents
                             :result result
                             :selected-rows selected-rows
                             :set-selected-rows set-selected-rows
                             :!selected-agents !selected-agents}]]
               [:button {:on-click #(optimize {:variables {:planId plan-id}})} "Optimize"]))]))))
