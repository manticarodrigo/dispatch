(ns ui.views.route.list
  (:require
   ["@apollo/client" :refer (gql useQuery)]
   [react-feather :rename {GitPullRequest RouteIcon
                           ChevronRight ChevronRightIcon
                           Plus PlusIcon}]
   [date-fns :as d]
   [reagent.core :as r]
   [shadow.resource :refer (inline)]
   [cljs-bean.core :refer (->clj)]
   [ui.lib.router :refer (link)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.input :refer (input)]
   [ui.components.inputs.generic.button :refer (button-class)]))

(defn item [{:keys [seat startAt]}]
  (let [{:keys [name]} seat
        start-date (-> (js/parseInt startAt) js/Date.)
        started? (-> start-date (d/isBefore (js/Date.)))]
    [:div {:class "flex justify-between w-full text-left"}
     [:div {:class "flex items-center"}
      [:div {:class "mr-2"} [:> RouteIcon {:class "w-4 h-4"}]]]
     [:div {:class "w-full"}
      [:div {:class "font-medium text-sm"} name]
      [:div {:class "font-light text-xs text-neutral-400"}
       (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))]]
     [:div {:class "flex-shrink-0 flex items-center"}
      [:div {:class "flex flex-col items-end"}
       [:div {:class "flex items-center text-xs text-neutral-400"}
        [:div {:class (class-names
                       "mr-1 rounded-full w-2 h-2"
                       (if started? "bg-green-500" "bg-amber-500"))}]
        "Status"]
       [:div {:class "flex items-center text-xs text-left text-neutral-200"}
        (when-not started? "in ")
        (-> start-date d/formatDistanceToNowStrict)
        (when started? " ago")]]
      [:div {:class "ml-2"} [:> ChevronRightIcon]]]]))

(def FETCH_ROUTES (gql (inline "queries/route/fetch-all.graphql")))

(defn view []
  (let [!search (r/atom "")]
    (fn []
      (let [query (useQuery FETCH_ROUTES)
            {:keys [data loading]} (->clj query)
            routes (some-> data :routes)]
        [:div {:class (class-names padding)}
         [:div {:class "mb-4 flex justify-between"}
          [input {:aria-label "Search"
                  :value @!search
                  :placeholder "Search routes"
                  :class "w-full mr-2"
                  :on-text #(reset! !search %)}]
          [link
           {:to "/routes/create"
            :class button-class}
           [:span {:class "sr-only"} "Add route"]
           [:> PlusIcon]]]

         [:ul
          (for [{:keys [id] :as route} routes]
            ^{:key id}
            [:li
             [link {:to (str "/routes/" id)
                    :class (class-names "mb-2 block" button-class)}
              [item route]]])
          (when (and (not loading) (empty? routes)) [:p {:class "text-center"} "No routes found."])
          (when loading [:p {:class "text-center"} "Loading routes..."])]]))))
