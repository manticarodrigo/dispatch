(ns ui.views.organization.task.detail
  (:require [react]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.title :refer (title)]
            [ui.components.lists.stop :refer (stop-list)]))

(def FETCH_ORGANIZATION_TASK (gql (inline "queries/user/organization/fetch-task.graphql")))

(defn view []
  (let [{task-id :task} (use-params)
        query (use-query FETCH_ORGANIZATION_TASK {:variables {:taskId task-id}})
        {:keys [data loading]} query
        {:keys [task]} (some-> data :user :organization)
        {:keys [agent stops route startAt]} task
        {:keys [path]} route
        {:keys [name]} agent]

    (react/useEffect
     (fn []
       (dispatch [:map
                  {:paths (when path [path])
                   :points (->> stops
                                (mapv :place)
                                (mapv (fn [{:keys [lat lng name]}]
                                        {:title name
                                         :position {:lat lat :lng lng}})))}])
       #())
     #js[route stops])

    [map-layout
     [title {:title name :subtitle (tr [:status/start-at] [startAt])}]
     [stop-list {:task task :loading loading}]]))
