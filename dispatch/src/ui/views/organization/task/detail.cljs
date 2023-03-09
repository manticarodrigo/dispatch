(ns ui.views.organization.task.detail
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.lists.stop :refer (stop-list)]
            [ui.components.inputs.loading-button :refer (loading-button)]))

(def FETCH_ORGANIZATION_TASK (gql (inline "queries/user/organization/fetch-task.graphql")))
(def OPTIMIZE_TASK (gql (inline "mutations/task/optimize-task.graphql")))

(defn view []
  (let [{task-id :task} (use-params)
        query (use-query FETCH_ORGANIZATION_TASK {:variables {:taskId task-id}})
        [optimize-task optimize-task-status] (use-mutation OPTIMIZE_TASK {})
        {:keys [data loading]} query
        {:keys [task]} (some-> data :user :organization)
        {:keys [agent stops route startAt]} task
        {:keys [name]} agent]

    (use-map-items
     loading
     {:tasks [task]
      :places (mapv :place stops)}
     [route stops])

    [map-layout {:title name
                 :subtitle (tr [:status/start-at] [startAt])
                 :actions [loading-button
                           {:loading (:loading optimize-task-status)
                            :label (tr [:verb/optimize])
                            :class "capitalize"
                            :on-click #(optimize-task {:variables {:taskId task-id}})}]}
     [stop-list {:task task :loading loading}]]))
