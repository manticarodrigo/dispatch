(ns ui.views.admin.place.detail
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

(def FETCH_PLACE (gql (inline "queries/place/fetch.graphql")))

(defn view []
  (let [{place-id :place} (use-params)
        [{:keys [date status] :as search-params} set-search-params] (use-search-params)
        query (use-query
               FETCH_PLACE
               {:variables
                {:placeId place-id
                 :filters {:start (-> date parse-date d/startOfDay)
                           :end  (-> date parse-date d/endOfDay)
                           :status status}}})
        {:keys [data previousData loading]} query
        {:keys [tasks]} (:place data)
        {:keys [name description]} (or (:place previousData) (:place data))]

    (react/useEffect
     (fn []
       (dispatch [:map {:paths (mapv #(-> % :route :path) tasks)}])
       #())
     #js[tasks])

    [:div {:class (class-names padding)}
     [:div {:class "mb-4"}
      [:div {:class "text-lg font-medium"} name]
      [:div {:class "text-xs font-light"} description]]
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
      (for [{:keys [id startAt]} tasks]
        (let [start-date (-> (js/parseInt startAt) js/Date.)
              started? (-> start-date (d/isBefore (js/Date.)))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to (str "/admin/tasks/" id)
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
      (when (and (not loading) (empty? tasks)) [:p {:class "text-center"} "No tasks found."])
      (when loading [:p {:class "text-center"} "Loading tasks..."])]]))
