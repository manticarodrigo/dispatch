(ns ui.views.route.list
  (:require
   ["@apollo/client" :refer (gql)]
   [react-feather :rename {GitPullRequest RouteIcon
                           ChevronRight ChevronRightIcon
                           Plus PlusIcon}]
   [date-fns :as d]
   [clojure.string :as s]
   [shadow.resource :refer (inline)]
   [cljs-bean.core :refer (->clj)]
   [ui.lib.apollo :refer (use-query)]
   [ui.lib.router :refer (link use-search-params)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.input :refer (input)]
   [ui.components.inputs.generic.date :refer (date-select)]
   [ui.components.inputs.generic.button :refer (button-class)]
   [ui.components.inputs.generic.radio-group :refer (radio-group)]))

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
      [:div {:class "ml-2"} [:> ChevronRightIcon {:class "w-4 h-4"}]]]]))

(def FETCH_ROUTES (gql (inline "queries/route/fetch-all.graphql")))

(defn parse-date [date]
  (if date (-> date js/parseInt js/Date.) (js/Date.)))

(defn transform-date [date fn]
  (-> date parse-date fn .getTime str))

(defn view []
  (let [[search-params set-search-params] (use-search-params)
        variables {:filters
                   {:start (-> search-params :start (transform-date d/startOfDay))
                    :end  (-> search-params :end (transform-date d/endOfDay))}}
        query (use-query FETCH_ROUTES {:variables variables})
        {:keys [data loading]} (->clj query)
        routes (some-> data :routes)
        filtered-routes (if (empty? (-> search-params :text))
                          routes
                          (filter
                           #(s/includes?
                             (-> % :seat :name s/lower-case)
                             (s/lower-case (-> search-params :text)))
                           routes))]
    [:div {:class (class-names padding)}
     [:div {:class "mb-4"}
      [:div {:class "flex justify-between"}

       [input {:aria-label "Search"
               :value (-> search-params :text)
               :placeholder "Search routes"
               :class "mr-2 w-full"
               :on-text (fn [query]
                          (set-search-params (if (empty? query)
                                               (dissoc search-params :text)
                                               (assoc search-params :text query))))}]
       [link
        {:to "/routes/create"
         :class button-class}
        [:span {:class "sr-only"} "Add route"]
        [:> PlusIcon]]]
      [:div {:class "mt-2 flex"}
       [date-select {:class "w-1/2"
                     :label "Start date"
                     :value (-> (or (some-> search-params :start js/parseInt js/Date.)
                                    (js/Date.))
                                d/startOfDay)
                     :on-select #(set-search-params
                                  (assoc search-params :start (-> % .getTime)))}]
       [:span {:class "w-2"}]
       [date-select {:class "w-1/2"
                     :label "End date"
                     :value (-> (or (some-> search-params :end js/parseInt js/Date.)
                                    (js/Date.))
                                d/endOfDay)
                     :on-select #(set-search-params
                                  (assoc search-params :end (-> % .getTime)))}]]
      [:div {:class "mt-2"}
       [radio-group {:sr-label "Select status"
                     :value "all"
                     :options [{:key "all" :label "All" :value "all"}
                               {:key "active" :label "Active" :value "active"}
                               {:key "inactive" :label "Inactive" :value "inactive"}]
                     :on-change js/console.log}]]]

     [:ul
      (for [{:keys [id] :as route} filtered-routes]
        ^{:key id}
        [:li
         [link {:to (str "/routes/" id)
                :class (class-names "mb-2 block" button-class)}
          [item route]]])
      (when (and (not loading) (empty? filtered-routes)) [:p {:class "text-center"} "No routes found."])
      (when loading [:p {:class "text-center"} "Loading routes..."])]]))
