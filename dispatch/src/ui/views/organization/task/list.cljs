(ns ui.views.organization.task.list
  (:require [react]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.date :as d]
            [ui.utils.string :refer (filter-text)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]
            [ui.components.filters :refer (filters)]
            [ui.components.lists.task :refer (task-list)]))

(def FETCH_ORGANIZATION_TASKS (gql (inline "queries/user/organization/fetch-tasks.graphql")))

(defn view []
  (let [[{:keys [date text status] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query
                                FETCH_ORGANIZATION_TASKS
                                {:variables
                                 {:filters
                                  {:start (-> date parse-date d/startOfDay)
                                   :end  (-> date parse-date d/endOfDay)
                                   :status status}}})
        {:keys [tasks]} (some-> data :user :organization)
        filtered-tasks (filter-text text #(-> % :agent :name) tasks)]

    (react/useEffect
     (fn []
       (dispatch [:map {:paths (mapv #(-> % :route :path) filtered-tasks)}])
       #())
     #js[tasks text])

    [map-layout
     [header {:title (tr [:view.task.list/title])
              :create-link "create"}]
     [filters {:search text
               :on-search-change #(set-search-params
                                   (if (empty? %)
                                     (dissoc search-params :text)
                                     (assoc search-params :text %)))
               :date (-> date parse-date d/startOfDay)
               :on-date-change #(set-search-params
                                 (assoc search-params :date (-> % .getTime)))
               :status (or status "ALL")
               :on-status-change #(set-search-params
                                   (if (= % "ALL")
                                     (dissoc search-params :status)
                                     (assoc search-params :status %)))}]
     [task-list {:tasks filtered-tasks :loading loading}]]))
