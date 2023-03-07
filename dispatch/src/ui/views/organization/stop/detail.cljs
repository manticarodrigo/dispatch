(ns ui.views.organization.stop.detail
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.forms.stop :refer (stop-form)]))

(def FETCH_ORGANIZATION_STOP (gql (inline "queries/user/organization/fetch-stop.graphql")))

(defn view []
  (let [{stop-id :stop} (use-params)
        query (use-query FETCH_ORGANIZATION_STOP {:variables {:stopId stop-id}})
        {:keys [loading]} query
        {:keys [place]} (-> query :data :user :organization :stop)
        {:keys [name description]} place]

    (use-map-items
     loading
     {:places [place]}
     [place])

    [map-layout {:title name :subtitle description}
     [:div {:class "p-4 overflow-y-auto"}
      [stop-form]]]))
