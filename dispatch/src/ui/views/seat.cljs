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

(defn- route-leg [idx address]
  (let [delivered? (< idx 2)]
    [:div {:class (class-names  "py-2 flex")}
     [:div {:class (class-names "relative"
                                "shrink-0 flex justify-center items-center"
                                "rounded-full border border-neutral-300"
                                "w-6 h-6"
                                "font-bold bg-neutral-900")}

      (when delivered? [:> CheckIcon {:class "w-4 h-4"}])]
     [:div {:class "pl-2 lg:pl-6 flex w-full"}
      [:div {:class "w-full"}
       [:p {:class "text-base font-medium"} (str "Address " (+ 1 idx))]
       [:p {:class "text-xs text-neutral-300"} address]]
      [:div {:class "shrink-0 flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
       (if delivered?
         [:div {:class "flex"}
          [:> PackageIcon {:class "mr-1 w-4 h-4"}]
          "09:45pm"]
         [:<>
          [:div {:class "flex"}
           [:> DurationIcon {:class "mr-1 w-4 h-4"}]
           "10:15pm"]])]]]))

(defn route-card [{:keys [startAt stops]}]
  (let [startDate (-> (js/parseInt startAt) js/Date.)
        started? (-> startDate (d/isBefore (js/Date.)))]
    [:div {:class (class-names "mb-4" box-class)}
     [:p {:class (class-names
                  box-padding-class
                  "mb-2 rounded-t border-b border-neutral-500 bg-neutral-800")}
      [:div {:class "font-medium text-sm"}
       (if started? "Started" "Starts")
       " "
       (-> startDate (d/formatDistanceToNow #js{:addSuffix true}))]
      [:div {:class "font-light text-xs"}
       (-> startDate (d/format "yyyy/MM/dd hh:mmaaa"))]]
     [:ol {:class (class-names box-padding-class "overflow-y-auto")}
      (for [[idx {:keys [id address]}] (map-indexed vector stops)]
        [:li {:key id :class "relative"}
         [:span {:class "absolute left-3 border-l border-neutral-50 h-full"}]
         [route-leg idx (:description address)]])]]))

(def FETCH_SEAT (gql (inline "queries/seat/fetch.graphql")))

(defn view []
  (let [params (use-params)
        query (use-query FETCH_SEAT {:variables {:id (:id params)}})
        {:keys [data loading]} (->clj query)
        {:keys [name routes]} (:seat data)]
    [:div {:class (class-names padding)}
     (when loading [:p "Loading..."])
     [:div {:class "mb-4"}
      [:p {:class "text-lg font-medium"} name]
      [:p {:class "text-xs font-light"} "Last seen: " "2 minutes ago"]]
     (for [route routes]
       ^{:key (:id route)}
       [route-card route])]))
