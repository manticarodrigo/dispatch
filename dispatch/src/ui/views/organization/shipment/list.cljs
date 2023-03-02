(ns ui.views.organization.shipment.list
  (:require [react]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.lists.shipment :refer (shipment-list)]))

(def FETCH_ORGANIZATION_SHIPMENTS (gql (inline "queries/user/organization/fetch-shipments.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_ORGANIZATION_SHIPMENTS {})
        {:keys [shipments]} (some-> data :user :organization)]
    [:div {:class padding}
     [title {:title (tr [:view.shipment.list/title])
             :create-link "create"}]
     [shipment-list {:shipments shipments :loading loading}]]))
