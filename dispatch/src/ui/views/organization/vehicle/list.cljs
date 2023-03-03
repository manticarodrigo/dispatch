(ns ui.views.organization.vehicle.list
  (:require [react]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.lists.vehicle :refer (vehicle-list)]))

(def FETCH_ORGANIZATION_VEHICLES (gql (inline "queries/user/organization/fetch-vehicles.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_ORGANIZATION_VEHICLES {})
        {:keys [vehicles]} (some-> data :user :organization)]
    [:div {:class padding}
     [title {:title (tr [:view.vehicle.list/title])
             :create-link "create"}]
     [vehicle-list {:vehicles vehicles :loading loading}]]))
