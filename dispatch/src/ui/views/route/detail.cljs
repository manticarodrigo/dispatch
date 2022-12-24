(ns ui.views.route.detail
  (:require [react-feather :rename {Check CheckIcon
                                    Minus MinusIcon
                                    Package PackageIcon
                                    Clock ClockIcon}]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (link use-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.inputs.generic.button :refer (button-class)]
            [ui.components.list-item :refer (list-item)]))

(def FETCH_ROUTE (gql (inline "queries/route/fetch.graphql")))

(defn view []
  (let [params (use-params)
        query (use-query FETCH_ROUTE {:variables {:id (:id params)}})
        {:keys [data loading]} (->clj query)
        {:keys [seat]} (:route data)
        stops (-> data :route :stops reverse)
        {:keys [name location]} seat]
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
          [:li {:class (class-names "mb-2")}
           [link {:to (str "/stops/" id)
                  :class (class-names "block" button-class)}
            [list-item {:icon (if arrivedAt CheckIcon MinusIcon)
                        :title name
                        :subtitle description
                        :detail [:div {:class "shrink-0 flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
                                 (if arrivedAt
                                   [:div {:class "flex"}
                                    [:> PackageIcon {:class "mr-3 w-4 h-4 text-green-500"}]
                                    (-> arrivedAt (js/parseInt) (js/Date.) (d/format "hh:mmaaa"))]
                                   [:div {:class "flex"}
                                    [:> ClockIcon {:class "mr-3 w-4 h-4 text-neutral-500"}]
                                    (-> (js/Date.) (d/format "hh:mmaaa"))])]}]]]))]]))
