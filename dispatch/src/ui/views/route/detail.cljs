(ns ui.views.route.detail
  (:require [react]
            [react-feather :rename {Check CheckIcon
                                    Minus MinusIcon
                                    Package PackageIcon
                                    Clock ClockIcon}]
            [date-fns :as d]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.link-card :refer (link-card)]))

(def FETCH_ROUTE (gql (inline "queries/route/fetch.graphql")))

(defn view []
  (let [params (use-params)
        query (use-query FETCH_ROUTE {:variables {:id (:id params)}})
        {:keys [data loading]} query
        {:keys [seat route]} data
        stops (some-> route :stops reverse)
        path (some-> route :route :path)
        {:keys [name location]} seat
        markers (->> stops
                     (mapv :address)
                     (mapv (fn [{:keys [lat lng name]}]
                             (prn lat lng name)
                             {:position {:lat lat :lng lng}
                              :title name})))]

    (react/useEffect
     (fn []
       (dispatch [:map/set-paths (when path [path])])
       (dispatch [:map/set-points markers])
       #(do
          (dispatch [:map/set-paths nil])
          (dispatch [:map/set-points nil])))
     #js[route])

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
     [:ol
      (for [{:keys [id address arrivedAt]} stops]
        (let [{:keys [name description]} address]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card
            {:to (str "/stops/" id)
             :icon (if arrivedAt CheckIcon MinusIcon)
             :title name
             :subtitle description
             :detail [:div {:class "shrink-0 flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
                      (if arrivedAt
                        [:div {:class "flex"}
                         [:> PackageIcon {:class "mr-3 w-4 h-4 text-green-500"}]
                         (-> arrivedAt (js/parseInt) (js/Date.) (d/format "hh:mmaaa"))]
                        [:div {:class "flex"}
                         [:> ClockIcon {:class "mr-3 w-4 h-4 text-neutral-500"}]
                         (-> (js/Date.) (d/format "hh:mmaaa"))])]}]]))]]))
