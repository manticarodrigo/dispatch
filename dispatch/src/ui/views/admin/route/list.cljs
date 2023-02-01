(ns ui.views.admin.route.list
  (:require
   ["@apollo/client" :refer (gql)]
   [react]
   [react-feather :rename {GitPullRequest RouteIcon
                           Plus PlusIcon}]
   [date-fns :as d]
   [re-frame.core :refer (dispatch)]
   [clojure.string :as s]
   [shadow.resource :refer (inline)]
   [common.utils.date :refer (parse-date)]
   [ui.lib.apollo :refer (use-query)]
   [ui.lib.router :refer (link use-search-params)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.utils.i18n :refer (tr)]
   [ui.components.inputs.generic.input :refer (input)]
   [ui.components.inputs.generic.date :refer (date-select)]
   [ui.components.inputs.generic.radio-group :refer (radio-group)]
   [ui.components.link-card :refer (link-card)]))

(def FETCH_ROUTES (gql (inline "queries/route/fetch-all.graphql")))

(defn view []
  (let [[{:keys [date text status] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query
                                FETCH_ROUTES
                                {:variables
                                 {:filters
                                  {:start (-> date parse-date d/startOfDay)
                                   :end  (-> date parse-date d/endOfDay)
                                   :status status}}})
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
     [:div {:class "mb-4 flex justify-between items-center"}
      [:h1 {:class "text-lg"} (tr [:view.route.list/title])]
      [link {:to "/admin/routes/create" :class "underline text-sm"} [:> PlusIcon {:class "inline mr-1 w-3 h-3"}] "Create"]]
     [:div {:class "mb-2"}
      [:div {:class "flex justify-between"}
       [input {:aria-label "Search by name"
               :value (-> search-params :text)
               :placeholder "Search by name"
               :class "w-full"
               :on-text #(set-search-params (if (empty? %)
                                              (dissoc search-params :text)
                                              (assoc search-params :text %)))}]
       [:div {:class "w-2"}]
       [date-select {:label "Select date"
                     :value (-> (or (some-> search-params :date js/parseInt js/Date.)
                                    (js/Date.))
                                d/startOfDay)
                     :on-select #(set-search-params
                                  (assoc search-params :date (-> % .getTime)))}]]
      [:div {:class "mt-2"}
       [radio-group {:sr-label "Select status"
                     :value (or (-> search-params :status) "ALL")
                     :options [{:key "ALL" :label "All"}
                               {:key "INCOMPLETE" :label "Incomplete"}
                               {:key "COMPLETE" :label "Complete"}]
                     :on-change #(set-search-params (if (= % "ALL")
                                                      (dissoc search-params :status)
                                                      (assoc search-params :status %)))}]]]
     [:ul
      (for [{:keys [id seat startAt]} filtered-routes]
        (let [{:keys [name]} seat
              start-date (-> (js/parseInt startAt) js/Date.)
              started? (-> start-date (d/isBefore (js/Date.)))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to (str "/admin/routes/" id)
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
