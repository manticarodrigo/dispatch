(ns ui.views.organization.stop.detail
  (:require [react]
            [shadow.resource :refer (inline)]
            [re-frame.core :refer (dispatch)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.css :refer (padding)]
            [ui.components.title :refer (title)]
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

    [:div {:class padding}
     [title {:title name :subtitle description}]
     [stop-form]]))