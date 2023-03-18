(ns ui.views.organization.plan.list
  (:require ["react"]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.lists.plan :refer (plan-list)]))

(def FETCH_ORGANIZATION_PLANS (gql (inline "queries/user/organization/fetch-plans.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_ORGANIZATION_PLANS {})
        {:keys [plans]} (some-> data :user :organization)]

    (use-map-items loading {} [])

    [map-layout {:title (tr [:view.plan.list/title])
                 :create-link "create"}
     [plan-list {:plans plans :loading loading}]]))
