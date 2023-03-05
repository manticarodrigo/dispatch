(ns ui.views.organization.task.detail
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]
            [ui.components.lists.stop :refer (stop-list)]))

(def FETCH_ORGANIZATION_TASK (gql (inline "queries/user/organization/fetch-task.graphql")))

(defn view []
  (let [{task-id :task} (use-params)
        query (use-query FETCH_ORGANIZATION_TASK {:variables {:taskId task-id}})
        {:keys [data loading]} query
        {:keys [task]} (some-> data :user :organization)
        {:keys [agent stops route startAt]} task
        {:keys [name]} agent]

    (use-map-items
     loading
     {:tasks [task]
      :places (mapv :place stops)}
     [route stops])

    [map-layout
     [header {:title name :subtitle (tr [:status/start-at] [startAt])}]
     [stop-list {:task task :loading loading}]]))
