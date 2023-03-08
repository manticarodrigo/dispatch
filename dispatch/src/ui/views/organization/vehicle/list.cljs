(ns ui.views.organization.vehicle.list
  (:require [react]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.lists.vehicle :refer (vehicle-list)]))

(def FETCH_ORGANIZATION_VEHICLES (gql (inline "queries/user/organization/fetch-vehicles.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_ORGANIZATION_VEHICLES {})
        {:keys [vehicles]} (some-> data :user :organization)]

    (use-map-items loading {} [])

    [map-layout {:title (tr [:view.vehicle.list/title])
                 :create-link "create"}
     [vehicle-list {:vehicles vehicles :loading loading}]]))
