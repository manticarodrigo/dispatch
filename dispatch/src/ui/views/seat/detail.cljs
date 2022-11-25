(ns ui.views.seat.detail
  (:require [react-feather :rename {GitPullRequest RouteIcon
                                    Check CheckIcon
                                    Minus MinusIcon
                                    Package PackageIcon
                                    ChevronDown ChevronDownIcon}]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.inputs.generic.accordion :refer (accordion)]))

(defn item-term [{:keys [startAt]}]
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
      [:div {:class "ml-2"} [:> ChevronDownIcon]]]]))

(defn item-description [{:keys [stops]}]
  [:ol
   (for [{:keys [id address arrivedAt]} stops]
     (let [{:keys [name description]} address]
       ^{:key id}
       [:li {:class (class-names  "py-2 flex")}
        (if arrivedAt
          [:> CheckIcon {:class (class-names "shrink-0 flex justify-center items-center"
                                             "mt-1"
                                             "w-3 h-3"
                                             "text-green-500")}]
          [:> MinusIcon {:class (class-names "relative"
                                             "shrink-0 flex justify-center items-center"
                                             "mt-1"
                                             "w-3 h-3"
                                             "text-neutral-500")}])
        [:div {:class "pl-2 lg:pl-4 flex w-full"}
         [:div {:class "w-full"}
          [:div {:class "text-sm"} name]
          [:div {:class "font-light text-xs text-neutral-300"} description]]
         [:div {:class "shrink-0 flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
          (when arrivedAt
            [:div {:class "flex"}
             [:> PackageIcon {:class "mr-1 w-4 h-4"}]
             (-> arrivedAt (js/parseInt) (js/Date.) (d/format "hh:mmaaa"))])]]]))])

(def FETCH_SEAT (gql (inline "queries/seat/fetch.graphql")))

(defn view []
  (let [params (use-params)
        query (use-query FETCH_SEAT {:variables {:id (:id params)}})
        {:keys [data loading]} (->clj query)
        {:keys [name location routes]} (:seat data)]
    [:div {:class (class-names padding)}
     (when loading [:p "Loading..."])
     [:div {:class "mb-4"}
      [:div {:class "text-lg font-medium"} name]
      [:div {:class "text-xs font-light"}
       "Last seen "
       (if location
         (-> location :createdAt (js/parseInt) (d/formatRelative (js/Date.)))
         "never")]]
     [:div {:class "mb-4"}
      [:button {:class "mr-2 pb-1 border-b border-neutral-200 text-sm"} "Upcoming"]
      [:button {:class "mr-2 pb-1 border-b border-neutral-700 text-sm"} "Completed"]]
     [accordion {:items routes
                 :item-class "mb-2"
                 :item-to-term item-term
                 :item-to-description item-description}]]))
