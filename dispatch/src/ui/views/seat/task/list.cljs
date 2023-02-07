(ns ui.views.seat.task.list
  (:require [react]
            [react-feather :rename {GitPullRequest RouteIcon}]
            [date-fns :as d]
            [re-frame.core :refer (dispatch)]
            [clojure.string :as s]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params use-search-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.components.inputs.radio-group :refer (radio-group)]
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
        {:keys [tasks]} (:seat data)
        filtered-tasks (if (empty? text)
                         tasks
                         (filter
                          #(s/includes?
                            (-> % :seat :name s/lower-case)
                            (s/lower-case text))
                          tasks))]

    (react/useEffect
     (fn []
       (dispatch [:map {:paths (mapv #(-> % :route :path) filtered-tasks)}])
       #())
     #js[tasks text])

    [:div {:class (class-names padding)}
     [:div {:class "mb-4 flex justify-between items-center"}
      [:h1 {:class "text-lg"} (tr [:view.task.list/title])]]
     [:div {:class "mb-4"}
      [date-select {:label "Select date"
                    :value (-> (or (some-> search-params :date js/parseInt js/Date.)
                                   (js/Date.))
                               d/startOfDay)
                    :on-select #(set-search-params
                                 (assoc search-params :date (-> % .getTime)))}]
      [:div {:class "mt-2"}
       [radio-group {:sr-label "Select status"
                     :value (or (-> search-params :status) "ALL")
                     :options [{:key "ALL" :label "All"}
                               {:key "INCOMPLETE" :label "Incomplete"}
                               {:key "COMPLETE" :label "Complete"}]
                     :on-change #(set-search-params (if (= % "ALL")
                                                      (dissoc search-params :status)
                                                      (assoc search-params :status %)))}]]]
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
                       :detail [:<>
                                [:div {:class "flex items-center text-xs text-neutral-400"}
                                 [:div {:class (class-names
                                                "mr-1 rounded-full w-2 h-2"
                                                (if started? "bg-green-500" "bg-amber-500"))}]
                                 "Status"]
                                [:div {:class "flex items-center text-xs text-left text-neutral-200"}
                                 (if started? "Active" "Inactive")]]}]]))
      (when (and (not loading) (empty? filtered-tasks)) [:p {:class "text-center"} "No tasks found."])
      (when loading [:p {:class "text-center"} "Loading tasks..."])]]))
