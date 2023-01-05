(ns ui.views.seat.detail
  (:require [react-feather :rename {GitPullRequest RouteIcon
                                    ChevronRight ChevronRightIcon}]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params link)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.inputs.generic.button :refer (button-class)]))

(defn item [{:keys [startAt]}]
  (let [start-date (-> (js/parseInt startAt) js/Date.)
        started? (-> start-date (d/isBefore (js/Date.)))]
    [:div {:class "flex justify-between w-full text-left"}
     [:div {:class "flex items-center"}
      [:div {:class "mr-2"} [:> RouteIcon {:class "w-4 h-4"}]]]
     [:div {:class "w-full"}
      [:div {:class "font-medium text-sm"}
       (if started? "Started" "Starts in")
       " "
       (-> start-date d/formatDistanceToNowStrict)
       (when started? " ago")]
      [:div {:class "font-light text-xs text-neutral-400"}
       (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))]]
     [:div {:class "flex items-center"}
      [:div {:class "flex flex-col items-end"}
       [:div {:class "flex items-center text-xs text-neutral-400"}
        [:div {:class (class-names
                       "mr-1 rounded-full w-2 h-2"
                       (if started? "bg-green-500" "bg-amber-500"))}]
        "Status"]
       [:div {:class "flex items-center text-xs text-left text-neutral-200"}
        (if started? "Active" "Inactive")]]
      [:div {:class "ml-2"} [:> ChevronRightIcon {:class "w-4 h-4"}]]]]))

(def FETCH_SEAT (gql (inline "queries/seat/fetch.graphql")))

(defn list-upcoming []
  (println "upcoming")
  )

(defn list-completed []
  (println "completed")
)

(defn view []
  (let [params (use-params)
        query (use-query FETCH_SEAT {:variables {:id (:id params)}})
        {:keys [data loading]} query
        {:keys [name location routes]} (:seat data)]
    [:div {:class (class-names padding)}
     (when loading [:p "Loading..."])
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
     [:div {:class "mb-4"}
      [:button {:class "mr-2 pb-1 border-b border-neutral-200 text-sm" :on-click #(list-upcoming)} "Upcoming"]
      [:button {:class "mr-2 pb-1 border-b border-neutral-700 text-sm" :on-click #(list-completed)} "Completed" ]]
     [:ul
      (for [{:keys [id] :as route} routes]
        ^{:key id}
        [:li
         [link {:to (str "/routes/" id)
                :class (class-names "mb-2 block" button-class)}
          [item route]]])
      (when (and (not loading) (empty? routes)) [:p {:class "text-center"} "No routes found."])
      (when loading [:p {:class "text-center"} "Loading routes..."])]]))
