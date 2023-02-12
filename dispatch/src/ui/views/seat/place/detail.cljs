(ns ui.views.seat.place.detail
  (:require [react]
            [react-feather :rename {GitPullRequest RouteIcon}]
            [date-fns :as d]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params use-search-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.title :refer (title)]
            [ui.components.filters :refer (filters)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))

(def FETCH_PLACE (gql (inline "queries/place/fetch-by-device.graphql")))

(defn view []
  (let [{seat-id :seat place-id :place} (use-params)
        device (listen [:device])
        device-id (:id device)
        [{:keys [date status] :as search-params} set-search-params] (use-search-params)
        query (use-query
               FETCH_PLACE
               {:variables
                {:seatId seat-id
                 :deviceId device-id
                 :placeId place-id
                 :filters {:start (-> date parse-date d/startOfDay)
                           :end  (-> date parse-date d/endOfDay)
                           :status status}}})
        {:keys [data previousData loading]} query
        {:keys [tasks]} (:place data)
        {:keys [name description]} (or (:place previousData) (:place data))]

    (react/useEffect
     (fn []
       (dispatch [:map {:paths (mapv #(-> % :route :path) tasks)}])
       #())
     #js[tasks])

    [:div {:class (class-names padding)}
     [title {:title (or name "Loading name...")
             :description (or description "Loading description...")}]
     [filters {:date (-> date parse-date d/startOfDay)
               :on-date-change #(set-search-params
                                 (assoc search-params :date (-> % .getTime)))
               :status (or status "ALL")
               :on-status-change #(set-search-params (if (= % "ALL")
                                                       (dissoc search-params :status)
                                                       (assoc search-params :status %)))}]
     [:ul
      (for [{:keys [id startAt]} tasks]
        (let [start-date (-> (js/parseInt startAt) js/Date.)
              started? (-> start-date (d/isBefore (js/Date.)))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to (str "../tasks/" id)
                       :icon RouteIcon
                       :title (str (if started? "Started" "Starts in")
                                   " "
                                   (-> start-date d/formatDistanceToNowStrict)
                                   (when started? " ago"))
                       :subtitle (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))
                       :detail [status-detail
                                {:active? started?
                                 :text (if started? "Active" "Inactive")}]}]]))
      (if loading
        [:p {:class "text-center"} "Loading tasks..."]
        (when (empty? tasks)
          [:p {:class "text-center"} "No tasks found."]))]]))
