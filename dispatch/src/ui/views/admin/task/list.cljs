(ns ui.views.admin.task.list
  (:require [react]
            [react-feather :rename {GitPullRequest RouteIcon}]
            [date-fns :as d]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.string :refer (filter-text class-names)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.filters :refer (filters)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))

(def FETCH_TASKS (gql (inline "queries/task/fetch-all.graphql")))

(defn view []
  (let [[{:keys [date text status] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query
                                FETCH_TASKS
                                {:variables
                                 {:filters
                                  {:start (-> date parse-date d/startOfDay)
                                   :end  (-> date parse-date d/endOfDay)
                                   :status status}}})
        {:keys [tasks]} data
        filtered-tasks (filter-text text #(-> % :seat :name) tasks)]

    (react/useEffect
     (fn []
       (dispatch [:map {:paths (mapv #(-> % :route :path) filtered-tasks)}])
       #())
     #js[tasks text])

    [:div {:class (class-names padding)}
     [title {:title (tr [:view.task.list/title])
             :create-link "/admin/tasks/create"}]
     [filters {:search text
               :on-search-change #(set-search-params (if (empty? %)
                                                       (dissoc search-params :text)
                                                       (assoc search-params :text %)))
               :date (-> date parse-date d/startOfDay)
               :on-date-change #(set-search-params
                                 (assoc search-params :date (-> % .getTime)))
               :status (or status "ALL")
               :on-status-change #(set-search-params (if (= % "ALL")
                                                       (dissoc search-params :status)
                                                       (assoc search-params :status %)))}]
     [:ul
      (for [{:keys [id seat startAt]} filtered-tasks]
        (let [{:keys [name]} seat
              start-date (-> (js/parseInt startAt) js/Date.)
              started? (-> start-date (d/isBefore (js/Date.)))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to (str "/admin/tasks/" id)
                       :icon RouteIcon
                       :title name
                       :subtitle (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))
                       :detail [status-detail
                                {:active? started?
                                 :text (str (when-not started? "in ")
                                            (-> start-date d/formatDistanceToNowStrict)
                                            (when started? " ago"))}]}]]))
      (if loading
        [:p {:class "text-center"} "Loading tasks..."]
        (when (empty? filtered-tasks)
          [:p {:class "text-center"} "No tasks found."]))]]))
