(ns ui.views.seat
  (:require [react-feather :rename {Check CheckIcon
                                    Package PackageIcon
                                    Clock DurationIcon}]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.inputs.generic.button :refer (box-class box-padding-class)]))

(defn- route-leg [{:keys [address arrivedAt]}]
  (let [{:keys [name description]} address]
    [:div {:class (class-names  "py-2 flex")}
     [:div {:class (class-names "relative"
                                "shrink-0 flex justify-center items-center"
                                "rounded-full border border-neutral-300"
                                "w-6 h-6"
                                "font-bold bg-neutral-900")}

      (when arrivedAt [:> CheckIcon {:class "w-4 h-4"}])]
     [:div {:class "pl-2 lg:pl-6 flex w-full"}
      [:div {:class "w-full"}
       [:div {:class "text-base font-medium"} name]
       [:div {:class "text-xs text-neutral-300"} description]]
      [:div {:class "shrink-0 flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
       (if arrivedAt
         [:div {:class "flex"}
          [:> PackageIcon {:class "mr-1 w-4 h-4"}]
          (-> arrivedAt (js/parseInt) (js/Date.) (d/format "hh:mmaaa"))]
         [:<>
          [:div {:class "flex"}
           [:> DurationIcon {:class "mr-1 w-4 h-4"}]
           "10:15pm"]])]]]))

(defn route-card [{:keys [startAt stops]}]
  (let [start-date (-> (js/parseInt startAt) js/Date.)
        started? (-> start-date (d/isBefore (js/Date.)))]
    [:div {:class (class-names "mb-4" box-class)}
     [:div {:class (class-names
                    box-padding-class
                    "mb-2 rounded-t border-b border-neutral-500 bg-neutral-800")}
      [:div {:class "font-medium text-sm"}
       (if started? "Started" "Starts in")
       " "
       (-> start-date d/formatDistanceToNowStrict)
       (when started? " ago")]
      [:div {:class "font-light text-xs"}
       (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))]]
     [:ol {:class (class-names box-padding-class "overflow-y-auto")}
      (for [{:keys [id] :as stop} stops]
        [:li {:key id :class "relative"}
         [:span {:class "absolute left-3 border-l border-neutral-50 h-full"}]
         [route-leg stop]])]]))

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
      [:button {:class "mr-2 border-b border-neutral-50 text-sm"} "Upcoming"]
      [:button {:class "mr-2 border-b border-neutral-500 text-sm"} "Completed"]]
     [:ol
      (for [route routes]
        ^{:key (:id route)}
        [:li [route-card route]])]]))
