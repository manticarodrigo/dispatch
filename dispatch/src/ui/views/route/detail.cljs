(ns ui.views.route.detail
  (:require [react :as react]
            [react-feather :rename {Check CheckIcon
                                    Minus MinusIcon
                                    Package PackageIcon
                                    Clock ClockIcon}]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->js ->clj)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]))

(def FETCH_ROUTE (gql (inline "queries/route/fetch.graphql")))
(def CREATE_STOP_ARRIVAL (gql (inline "mutations/stop/create-stop-arrival.graphql")))

(defn view []
  (let [[note set-note] (react/useState "")
        params (use-params)
        query (use-query FETCH_ROUTE {:variables {:id (:id params)}})
        [create-stop-arrival] (use-mutation CREATE_STOP_ARRIVAL {})
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
      (for [{:keys [id address arrivedAt] :as stop} stops]
        (let [{:keys [name description]} address]
          ^{:key id}
          [:li {:class (class-names  "py-2 flex")}

           [:p "Note: " (:note stop)]
           [:form {:on-submit (fn [e]
                                (.preventDefault e)
                                (-> (create-stop-arrival (->js {:variables
                                                                {:stopId id
                                                                 :note note}}))))}
            [:input {:value note
                     :on-change (fn [e]
                                  (set-note (.. e -target -value)))}]
            [:button "enviar"]]

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
             (if arrivedAt
               [:div {:class "flex"}
                [:> PackageIcon {:class "mr-3 w-4 h-4"}]
                (-> arrivedAt (js/parseInt) (js/Date.) (d/format "hh:mmaaa"))]
               [:div {:class "flex"}
                [:> ClockIcon {:class "mr-3 w-4 h-4"}]
                (-> (js/Date.) (d/format "hh:mmaaa"))])]]]))]]))
