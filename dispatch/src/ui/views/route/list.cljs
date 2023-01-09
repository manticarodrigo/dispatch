(ns ui.views.route.list
  (:require
   ["@apollo/client" :refer (gql)]
   [react]
   [react-feather :rename {GitPullRequest RouteIcon
                           Plus PlusIcon}]
   [date-fns :as d]
   [re-frame.core :refer (dispatch)]
   [clojure.string :as s]
   [shadow.resource :refer (inline)]
   [ui.lib.apollo :refer (use-query)]
   [ui.lib.router :refer (link use-search-params)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.input :refer (input)]
   [ui.components.inputs.generic.date :refer (date-select)]
   [ui.components.inputs.generic.button :refer (button-class)]
   [ui.components.inputs.generic.radio-group :refer (radio-group)]
   [ui.components.link-card :refer (link-card)]))

(def FETCH_ROUTES (gql (inline "queries/route/fetch-all.graphql")))

(defn parse-date [date]
  (if date (-> date js/parseInt js/Date.) (js/Date.)))

(defn view []
  (let [[{:keys [start end text] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query
                                FETCH_ROUTES
                                {:variables
                                 {:filters
                                  {:start (-> start parse-date d/startOfDay)
                                   :end  (-> end parse-date d/endOfDay)}}})
        {:keys [routes]} data
        filtered-routes (if (empty? text)
                          routes
                          (filter
                           #(s/includes?
                             (-> % :seat :name s/lower-case)
                             (s/lower-case text))
                           routes))]

    (react/useEffect
     (fn []
       (dispatch [:map/set-paths (mapv #(-> % :route :path) filtered-routes)])
       #(dispatch [:map/set-paths nil]))
     #js[routes text])

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
      (for [{:keys [id seat startAt]} filtered-routes]
        (let [{:keys [name]} seat
              start-date (-> (js/parseInt startAt) js/Date.)
              started? (-> start-date (d/isBefore (js/Date.)))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to (str "/routes/" id)
                       :icon RouteIcon
                       :title name
                       :subtitle (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))
                       :detail [:<>
                                [:div {:class "flex items-center text-xs text-neutral-400"}
                                 [:div {:class (class-names
                                                "mr-1 rounded-full w-2 h-2"
                                                (if started? "bg-green-500" "bg-amber-500"))}]
                                 "Status"]
                                [:div {:class "flex items-center text-xs text-left text-neutral-200"}
                                 (when-not started? "in ")
                                 (-> start-date d/formatDistanceToNowStrict)
                                 (when started? " ago")]]}]]))
      (when (and (not loading) (empty? filtered-routes)) [:p {:class "text-center"} "No routes found."])
      (when loading [:p {:class "text-center"} "Loading routes..."])]]))
