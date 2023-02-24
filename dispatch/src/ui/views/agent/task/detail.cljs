(ns ui.views.agent.task.detail
  (:require [react]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.css :refer (padding)]
            [ui.components.title :refer (title)]
            [ui.components.lists.stop :refer (stop-list)]))

(def FETCH_TASK (gql (inline "queries/task/fetch-by-device.graphql")))

(defn view []
  (let [{agent-id :agent task-id :task} (use-params)
        device (listen [:device])
        device-id (:id device)
        query (use-query FETCH_TASK {:variables
                                     {:taskId task-id
                                      :agentId agent-id
                                      :deviceId device-id}})
        {:keys [data loading]} query
        {:keys [task]} data
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

    [:div {:class padding}
     [title {:title name :subtitle (tr [:status/start-at] [startAt])}]
     [stop-list {:task task :loading loading}]]))