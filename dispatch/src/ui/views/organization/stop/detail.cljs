(ns ui.views.organization.stop.detail
  (:require [react]
            [shadow.resource :refer (inline)]
            [re-frame.core :refer (dispatch)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]
            [ui.components.forms.stop :refer (stop-form)]))

(def FETCH_ORGANIZATION_STOP (gql (inline "queries/user/organization/fetch-stop.graphql")))

(defn view []
  (let [{stop-id :stop} (use-params)
        query (use-query FETCH_ORGANIZATION_STOP {:variables {:stopId stop-id}})
        {:keys [place]} (-> query :data :user :organization :stop)
        {:keys [name description lat lng]} place]

    (react/useEffect
     (fn []
       (dispatch [:map
                  {:points
                   (when place
                     [{:title name
                       :position {:lat lat :lng lng}}])}])
       #())
     #js[name lat lng])

    [map-layout
     [header {:title name :subtitle description}]
     [:div {:class "p-4 overflow-y-auto"}
      [stop-form]]]))
