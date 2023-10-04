(ns ui.views.agent.task.list
  (:require [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.filters :refer (filters)]
            [ui.components.lists.task :refer (task-list)]))

(def FETCH_AGENT_TASKS (gql (inline "queries/user/agent/fetch-tasks.graphql")))

(defn view []
  (let [[{:keys [date status] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query
                                FETCH_AGENT_TASKS
                                {:variables
                                 {:filters
                                  {:start (-> date parse-date d/startOfDay)
                                   :end  (-> date parse-date d/endOfDay)
                                   :status status}}})
        {:keys [tasks]} (some-> data :user :agent)]

    (use-map-items
     loading
     {:tasks tasks}
     [tasks])

    [map-layout {:title (tr [:view.task.list/title])}
     [filters {:date (-> date parse-date d/startOfDay)
               :on-date-change #(set-search-params
                                 (assoc search-params :date (-> % .getTime)))
               :status (or status "ALL")
               :on-status-change #(set-search-params
                                   (if (= % "ALL")
                                     (dissoc search-params :status)
                                     (assoc search-params :status %)))}]
     [task-list {:tasks tasks :loading loading}]]))
