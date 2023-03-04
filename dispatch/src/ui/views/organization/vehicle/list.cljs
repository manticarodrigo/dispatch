(ns ui.views.organization.vehicle.list
  (:require [react]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]
            [ui.components.lists.vehicle :refer (vehicle-list)]))

(def FETCH_ORGANIZATION_VEHICLES (gql (inline "queries/user/organization/fetch-vehicles.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_ORGANIZATION_VEHICLES {})
        {:keys [vehicles]} (some-> data :user :organization)]
    [map-layout
     [header {:title (tr [:view.vehicle.list/title])
             :create-link "create"}]
     [vehicle-list {:vehicles vehicles :loading loading}]]))
