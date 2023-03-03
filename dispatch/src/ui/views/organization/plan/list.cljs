(ns ui.views.organization.plan.list
  (:require [react]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.title :refer (title)]
            [ui.components.lists.plan :refer (plan-list)]))

(def FETCH_ORGANIZATION_PLANS (gql (inline "queries/user/organization/fetch-plans.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_ORGANIZATION_PLANS {})
        {:keys [plans]} (some-> data :user :organization)]
    [map-layout
     [title {:title (tr [:view.plan.list/title])
             :create-link "create"}]
     [plan-list {:plans plans :loading loading}]]))
