(ns ui.views.admin.seat.detail
  (:require [react]
            [react-feather :rename {GitPullRequest RouteIcon}]
            [date-fns :as d]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params use-search-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.components.inputs.radio-group :refer (radio-group)]
            [ui.components.link-card :refer (link-card)]))

(def FETCH_SEAT (gql (inline "queries/seat/fetch.graphql")))

(defn view []
  (let [{:keys [id]} (use-params)
        [{:keys [date status] :as search-params} set-search-params] (use-search-params)
        query (use-query
               FETCH_SEAT
               {:variables
                {:id id
                 :filters {:start (-> date parse-date d/startOfDay)
                           :end  (-> date parse-date d/endOfDay)
                           :status status}}})
        {:keys [data previousData loading]} query
        {:keys [routes]} (:seat data)
        {:keys [name location]} (or (:seat previousData) (:seat data))]

    (react/useEffect
     (fn []
       (dispatch [:map/set-paths (mapv #(-> % :route :path) routes)])
       #(dispatch [:map/set-paths nil]))
     #js[routes])

    [:div {:class (class-names padding)}
     [:div {:class "mb-4"}
      [:div {:class "text-lg font-medium"} name]
      [:div {:class "text-xs font-light"}
       "Last seen "
       (if location
         (str
          (-> location :createdAt (js/parseInt) (d/formatRelative (js/Date.)))
          " ("
          (-> location :createdAt (js/parseInt) (d/formatDistanceToNowStrict #js{:addSuffix true}))
          ")")
         "never")]]
     [:div {:class "mb-2"}
      [:div {:class "mt-2"}
       [date-select {:label "Select date"
                     :value (-> (or (some-> search-params :date js/parseInt js/Date.)
                                    (js/Date.))
                                d/startOfDay)
                     :on-select #(set-search-params
                                  (assoc search-params :date (-> % .getTime)))}]]
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
      (for [{:keys [id startAt]} routes]
        (let [start-date (-> (js/parseInt startAt) js/Date.)
              started? (-> start-date (d/isBefore (js/Date.)))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to (str "/admin/routes/" id)
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
      (when (and (not loading) (empty? routes)) [:p {:class "text-center"} "No routes found."])
      (when loading [:p {:class "text-center"} "Loading routes..."])]]))
