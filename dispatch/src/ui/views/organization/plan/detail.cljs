(ns ui.views.organization.plan.detail
  (:require [react :refer (useState)]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.header :refer (header)]
            [ui.components.tables.plan :refer (plan-table)]))

(def FETCH_PLAN (gql (inline "queries/user/organization/fetch-plan.graphql")))
(def OPTIMIZE_PLAN (gql (inline "mutations/plan/optimize.graphql")))

(defn sub-view [{:keys [plan agents]}]
  (let [!selected-agents (r/atom (into {}
                                       (for [[idx {:keys [id]}]
                                             (map-indexed vector agents)]
                                         {idx id})))]
    (fn []
      (let [{:keys [result]} plan
            [selected-rows set-selected-rows] (useState #js{})]
        [:div {:class "overflow-auto h-full"}
         [plan-table {:agents agents
                      :result result
                      :selected-rows selected-rows
                      :set-selected-rows set-selected-rows
                      :!selected-agents !selected-agents}]]))))

(defn view []
  (let [{plan-id :plan} (use-params)
        {:keys [data loading]} (use-query FETCH_PLAN {:variables {:planId plan-id}})
        [optimize] (use-mutation OPTIMIZE_PLAN {})
        {:keys [plan agents]} (-> data :user :organization)
        {:keys [result startAt endAt]} plan]
    [:main {:class "flex flex-col w-full h-full"}
     [header {:title (if loading
                       (str (tr [:misc/loading]) "...")
                       (tr [:view.plan.detail/title] [startAt endAt]))}]
     (if loading (str (tr [:misc/loading]) "...")
         (if result
           [sub-view {:plan plan :agents agents}]
           [:button {:on-click #(optimize {:variables {:planId plan-id}})} "Optimize"]))]))
