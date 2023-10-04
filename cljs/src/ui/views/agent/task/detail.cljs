(ns ui.views.agent.task.detail
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.lists.stop :refer (stop-list)]))

(def FETCH_AGENT_TASK (gql (inline "queries/user/agent/fetch-task.graphql")))

(defn view []
  (let [{task-id :task} (use-params)
        query (use-query FETCH_AGENT_TASK {:variables {:taskId task-id}})
        {:keys [data loading]} query
        {:keys [task]} (some-> data :user :agent)
        {:keys [agent stops startAt]} task
        {:keys [name]} agent]

    (use-map-items
     loading
     {:tasks [task]
      :places (mapv :place stops)}
     [task])

    [map-layout {:title name :subtitle (tr [:status/start-at] [startAt])}
     [stop-list {:task task :loading loading}]]))
