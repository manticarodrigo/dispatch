(ns ui.views.organization.shipment.list
  (:require [react]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.title :refer (title)]
            [ui.components.lists.shipment :refer (shipment-list)]))

(def FETCH_ORGANIZATION_SHIPMENTS (gql (inline "queries/user/organization/fetch-shipments.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_ORGANIZATION_SHIPMENTS {})
        {:keys [shipments]} (some-> data :user :organization)]
    [map-layout
     [title {:title (tr [:view.shipment.list/title])
             :create-link "create"}]
     [shipment-list {:shipments shipments :loading loading}]]))
