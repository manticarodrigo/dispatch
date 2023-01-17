(ns ui.views.address.detail
  (:require [react]
            [react-feather :rename {GitPullRequest RouteIcon}]
            [date-fns :as d]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.link-card :refer (link-card)]))

(def FETCH_ADDRESS (gql (inline "queries/address/fetch.graphql")))

(defn view []
  (let [params (use-params)
        query (use-query FETCH_ADDRESS {:variables {:id (:id params)}})
        {:keys [data loading]} query
        {:keys [name description routes]} (:address data)]

    (react/useEffect
     (fn []
       (dispatch [:map/set-paths (mapv #(-> % :route :path) routes)])
       #(dispatch [:map/set-paths nil]))
     #js[routes])

    [:div {:class (class-names padding)}
     (when loading [:p "Loading..."])
     [:div {:class "mb-4"}
      [:div {:class "text-lg font-medium"} name]
      [:div {:class "text-xs font-light"} description]]
     [:div {:class "mb-4"}
      [:button {:class "mr-2 pb-1 border-b border-neutral-200 text-sm"} "Upcoming"]
      [:button {:class "mr-2 pb-1 border-b border-neutral-700 text-sm"} "Completed"]]
     [:ul
      (for [{:keys [id startAt]} routes]
        (let [start-date (-> (js/parseInt startAt) js/Date.)
              started? (-> start-date (d/isBefore (js/Date.)))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to (str "/routes/" id)
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