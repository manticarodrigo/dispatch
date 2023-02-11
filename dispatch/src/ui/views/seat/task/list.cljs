(ns ui.views.seat.task.list
  (:require [react]
            [react-feather :rename {GitPullRequest RouteIcon}]
            [date-fns :as d]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params use-search-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.filters :refer (filters)]
            [ui.components.status-detail :refer (status-detail)]
            [ui.components.link-card :refer (link-card)]))

(def FETCH_SEAT (gql (inline "queries/seat/fetch-by-device.graphql")))

(defn view []
  (let [{seat-id :seat} (use-params)
        device (listen [:device])
        device-id (:id device)
        [{:keys [date text status] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query
                                FETCH_SEAT
                                {:variables
                                 {:seatId seat-id
                                  :deviceId device-id
                                  :filters
                                  {:start (-> date parse-date d/startOfDay)
                                   :end  (-> date parse-date d/endOfDay)
                                   :status status}}})
        {:keys [tasks]} (:seat data)]
    (react/useEffect
     (fn []
       (dispatch [:map {:paths (mapv #(-> % :route :path) tasks)}])
       #())
     #js[tasks text])

    [:div {:class (class-names padding)}
     [title {:title (tr [:view.task.list/title])}]
     [filters {:date (-> date parse-date d/startOfDay)
               :on-date-change #(set-search-params
                                 (assoc search-params :date (-> % .getTime)))
               :status (or status "ALL")
               :on-status-change #(set-search-params (if (= % "ALL")
                                                       (dissoc search-params :status)
                                                       (assoc search-params :status %)))}]
     [:ul
      (for [{:keys [id startAt]} tasks]
        (let [start-date (-> (js/parseInt startAt) js/Date.)
              started? (-> start-date (d/isBefore (js/Date.)))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to (str "/seat/" seat-id "/tasks/" id)
                       :icon RouteIcon
                       :title (str (if started? "Started" "Starts in")
                                   " "
                                   (-> start-date d/formatDistanceToNowStrict)
                                   (when started? " ago"))
                       :subtitle (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))
                       :detail  [status-detail {:active? started?
                                                :text (if started? "Started" "Not started")}]}]]))
      (if loading
        [:p {:class "text-center"} "Loading tasks..."]
        (when (empty? tasks)
          [:p {:class "text-center"} "No tasks found."]))]]))
