(ns ui.views.organization.shipment.list
  (:require ["react"]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.tables.shipment :refer (shipment-table)]))

(def FETCH_ORGANIZATION_SHIPMENTS (gql (inline "queries/user/organization/fetch-shipments.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_ORGANIZATION_SHIPMENTS {})
        {:keys [shipments]} (some-> data :user :organization)]
    [bare-layout {:title (tr [:view.shipment.list/title])
                  :create-link "create"}
     (if loading
       [:div {:class "p-4"} (tr [:misc/loading]) "..."]
       [shipment-table {:shipments shipments}])]))
