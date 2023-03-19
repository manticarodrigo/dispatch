(ns ui.views.organization.plan.detail
  (:require ["react" :refer (useState useEffect)]
            ["react-feather" :rename {Eye ViewIcon
                                      Edit CreateIcon
                                      Search SearchIcon}]
            [clojure.string :as s]
            [reagent.core :as r]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.tables.route :refer (route-table)]
            [ui.components.tables.shipment :refer (shipment-table)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.loading-button :refer (loading-button)]
            [ui.components.modal :refer (modal)]))

(def FETCH_PLAN (gql (inline "queries/user/organization/fetch-plan.graphql")))
(def OPTIMIZE_PLAN (gql (inline "mutations/plan/optimize.graphql")))
(def CREATE_PLAN_TASKS (gql (inline "mutations/plan/create-plan-tasks.graphql")))

(defn view []
  (let [!selected-agents (r/atom {})]
    (fn []
      (let [{plan-id :plan} (use-params)
            {:keys [data loading refetch]} (use-query FETCH_PLAN {:variables {:planId plan-id}})
            [optimize optimize-status] (use-mutation OPTIMIZE_PLAN {})
            [create-tasks create-tasks-status] (use-mutation CREATE_PLAN_TASKS {})
            {:keys [plan agents]} (-> data :user :organization)
            {:keys [result startAt endAt vehicles shipments]} plan
            {:keys [routes skipped]} result
            [selected-rows set-selected-rows] (useState #js{})
            [modal-open? set-modal-open?] (useState false)
            [search-term set-search-term] (useState nil)
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

        [bare-layout
         {:title (if loading
                   (str (tr [:misc/loading]) "...")
                   (tr [:view.plan.detail/title] [startAt endAt]))
          :actions [:<>
                    [button {:label [:div {:class "flex items-center"}
                                     [:> ViewIcon {:class "mr-2 w-4 h-4"}]
                                     (str (s/capitalize (tr [:adjective/skipped])) " (" (count skipped) ")")]
                             :class "shrink-0 mr-2"
                             :on-click #(set-modal-open? true)}]
                    [modal {:show modal-open?
                            :title (str (s/capitalize (tr [:adjective/skipped])) " " (tr [:noun/shipments]))
                            :on-close #(set-modal-open? false)}
                     [shipment-table {:shipments skipped}]]]
          :on-refresh #(refetch {:variables {:planId plan-id}})}
         (if loading [:div {:class "p-4"}
                      (tr [:misc/loading]) "..."]
             (if result
               [:<>
                [:div {:class "flex flex-col w-full h-full min-w-0 min-h-0"}
                 [:div {:class "flex items-center border-b border-neutral-700 p-4 w-full overflow-y-hidden overflow-x-auto"}
                  [input {:icon SearchIcon
                          :aria-label (tr [:field/search])
                          :placeholder (tr [:field/search])
                          :value search-term
                          :class "shrink-0 mr-2"
                          :on-text #(set-search-term %)}]
                  [loading-button
                   {:disabled (empty? selected-indexes)
                    :loading (:loading create-tasks-status)
                    :label [:div {:class "flex items-center"}
                            [:> CreateIcon {:class "mr-2 w-4 h-4"}]
                            (tr [:view.plan.create/title])
                            " "
                            (tr [:noun/tasks])
                            (when (> (count selected-indexes) 0)
                              (str " (" (count selected-indexes) ")"))]
                    :class "shrink-0 mr-2"
                    :on-click #(do
                                 (create-tasks
                                  {:variables
                                   {:planId plan-id
                                    :assignments
                                    (map
                                     (fn [idx]
                                       {:agentId (get @!selected-agents (int idx))
                                        :routeIndex idx})
                                     selected-indexes)}})
                                 (set-selected-rows #js{}))}]]
                 [:div {:class "w-full h-full min-w-0 min-h-0 overflow-auto"}
                  [route-table {:agents agents
                                :result routes
                                :search-term search-term
                                :set-search-term set-search-term
                                :selected-rows selected-rows
                                :set-selected-rows set-selected-rows
                                :!selected-agents !selected-agents
                                :on-create-task #(do
                                                   (create-tasks
                                                    {:variables
                                                     {:planId plan-id
                                                      :assignments
                                                      [{:agentId (get @!selected-agents (int %))
                                                        :routeIndex %}]}})
                                                   (set-selected-rows #js{}))}]]]]
               [:div {:class "p-4"}
                [:div (count vehicles) " " (tr [:noun/vehicles])]
                [:div (count shipments) " " (tr [:noun/shipments])]
                [loading-button
                 {:loading (:loading optimize-status)
                  :label (tr [:verb/optimize])
                  :class "mt-4 capitalize"
                  :on-click #(optimize {:variables {:planId plan-id}})}]]))]))))
