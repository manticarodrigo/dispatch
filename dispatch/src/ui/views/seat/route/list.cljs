(ns ui.views.seat.route.list
  (:require
   ["@apollo/client" :refer (gql)]
   [react]
   [react-feather :rename {GitPullRequest RouteIcon}]
   [date-fns :as d]
   [re-frame.core :refer (dispatch)]
   [clojure.string :as s]
   [shadow.resource :refer (inline)]
   [common.utils.date :refer (parse-date)]
   [ui.subs :refer (listen)]
   [ui.lib.apollo :refer (use-query use-mutation parse-anoms)]
   [ui.lib.router :refer (use-params use-search-params)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.utils.i18n :refer (tr)]
   [ui.components.modal :refer (modal)]
   [ui.components.inputs.generic.button :refer (button)]
   [ui.components.inputs.generic.date :refer (date-select)]
   [ui.components.inputs.generic.radio-group :refer (radio-group)]
   [ui.components.link-card :refer (link-card)]))

(def FETCH_SEAT (gql (inline "queries/seat/fetch-by-device.graphql")))
(def LINK_DEVICE (gql (inline "mutations/device/link.graphql")))

(defn view []
  (let [{seat-id :id} (use-params)
        device (listen [:device])
        device-id (:id device)
        device-info (:info device)
        [{:keys [date text status] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading error refetch]} (use-query
                                              FETCH_SEAT
                                              {:variables
                                               {:id seat-id
                                                :token device-id
                                                :filters
                                                {:start (-> date parse-date d/startOfDay)
                                                 :end  (-> date parse-date d/endOfDay)
                                                 :status status}}})
        [link-device] (use-mutation LINK_DEVICE {:refetchQueries [{:query FETCH_SEAT}]})
        {:keys [routes]} (:seat data)
        filtered-routes (if (empty? text)
                          routes
                          (filter
                           #(s/includes?
                             (-> % :seat :name s/lower-case)
                             (s/lower-case text))
                           routes))
        anoms (parse-anoms error)
        unlinked? (some? (some #(= (:reason %) "device-not-linked") anoms))
        invalid? (some? (some #(= (:reason %) "invalid-token") anoms))]

    (react/useEffect
     (fn []
       (dispatch [:map/set-paths (mapv #(-> % :route :path) filtered-routes)])
       #(dispatch [:map/set-paths nil]))
     #js[routes text])

    [:<>
     [modal {:show invalid? :title "Another device already linked" :on-close #()}
      [:p {:class "mb-4"} "Looks like this seat has a device linked to it already. If you would like to link your device to this seat, please reach out to an admin and ask them to unlink the other device first."]]
     [modal {:show unlinked? :title "No device linked" :on-close #()}
      [:p {:class "mb-4"} "Looks like this seat has no device linked to it yet. Please press the button below to link your device and continue."]
      [button {:label "Link Device"
               :class "bg-neutral-900 text-white px-4 py-2 rounded-md"
               :on-click (fn []
                           (-> (link-device {:variables {:seatId seat-id
                                                         :token device-id
                                                         :info device-info}})
                               (.then #(refetch))))}]]
     [:div {:class (class-names padding)}
      [:div {:class "mb-4 flex justify-between items-center"}
       [:h1 {:class "text-lg"} (tr [:view.route.list/title])]]
      [:div {:class "mb-4"}
       [date-select {:label "Select date"
                     :value (-> (or (some-> search-params :date js/parseInt js/Date.)
                                    (js/Date.))
                                d/startOfDay)
                     :on-select #(set-search-params
                                  (assoc search-params :date (-> % .getTime)))}]
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
       (for [{:keys [id startAt]} routes]
         (let [start-date (-> (js/parseInt startAt) js/Date.)
               started? (-> start-date (d/isBefore (js/Date.)))]
           ^{:key id}
           [:li {:class "mb-2"}
            [link-card {:to (str "/seat/" seat-id "/routes/" id)
                        :icon RouteIcon
                        :title (str (if started? "Started" "Starts in")
                                    " "
                                    (-> start-date d/formatDistanceToNowStrict)
                                    (when started? " ago"))
                        :subtitle (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))
                        :detail [:<>
                                 [:div {:class "flex items-center text-xs text-neutral-400"}
                                  [:div {:class (class-names
                                                 "mr-1 rounded-full w-2 h-2"
                                                 (if started? "bg-green-500" "bg-amber-500"))}]
                                  "Status"]
                                 [:div {:class "flex items-center text-xs text-left text-neutral-200"}
                                  (if started? "Active" "Inactive")]]}]]))
       (when (and (not loading) (empty? routes)) [:p {:class "text-center"} "No routes found."])
       (when loading [:p {:class "text-center"} "Loading routes..."])]]]))
