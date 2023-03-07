(ns ui.views.organization.plan.detail
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.header :refer (header)]
            [ui.components.tables.plan :refer (plan-table)]))

(def FETCH_PLAN (gql (inline "queries/user/organization/fetch-plan.graphql")))
(def OPTIMIZE_PLAN (gql (inline "mutations/plan/optimize.graphql")))

(defn view []
  (let [{plan-id :plan} (use-params)
        {:keys [data loading]} (use-query FETCH_PLAN {:variables {:planId plan-id}})
        [optimize] (use-mutation OPTIMIZE_PLAN {})
        {:keys [result startAt endAt]} (-> data :user :organization :plan)]
    [:main {:class "flex flex-col w-full h-full"}
     [header {:title (if loading
                       (str (tr [:misc/loading]) "...")
                       (tr [:view.plan.detail/title] [startAt endAt]))}]
     (if loading (str (tr [:misc/loading]) "...")
         (if result
           [:div {:class "overflow-auto h-full"}
            [plan-table {:result result}]]
           [:button {:on-click #(optimize {:variables {:planId plan-id}})} "Optimize"]))]))
