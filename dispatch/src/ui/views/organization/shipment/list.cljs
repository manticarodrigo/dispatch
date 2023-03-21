(ns ui.views.organization.shipment.list
  (:require ["react" :refer (useState)]
            ["react-feather" :rename {Upload UploadIcon
                                      Archive ArchiveIcon
                                      CornerUpLeft UnarchiveIcon
                                      Edit CreateIcon
                                      Search SearchIcon}]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.modal :refer (modal)]
            [ui.components.table :refer (data->selected-row-ids)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.components.inputs.radio-group :refer (radio-group)]
            [ui.components.inputs.loading-button :refer (loading-button)]
            [ui.components.forms.shipment :refer (shipment-form)]
            [ui.components.forms.upload-shipments :refer (upload-shipments-form)]
            [ui.components.tables.shipment :refer (shipment-table)]))

(def FETCH_ORGANIZATION_SHIPMENTS (gql (inline "queries/user/organization/fetch-shipments.graphql")))
(def ARCHIVE_SHIPMENTS (gql (inline "mutations/shipment/archive-shipments.graphql")))
(def UNARCHIVE_SHIPMENTS (gql (inline "mutations/shipment/unarchive-shipments.graphql")))

(defn view []
  (let [[{:keys [date text status] :as search-params} set-search-params] (use-search-params)
        [state set-state] (useState {})
        [selected-rows set-selected-rows] (useState #js{})
        {:keys [create-open? upload-open?]} state
        {:keys [data loading]} (use-query FETCH_ORGANIZATION_SHIPMENTS {:variables
                                                                        {:filters
                                                                         {:start (-> date parse-date d/startOfDay)
                                                                          :end (-> date parse-date d/endOfDay)
                                                                          :status status}}})
        [archive archive-status] (use-mutation ARCHIVE_SHIPMENTS {:refetchQueries [{:query FETCH_ORGANIZATION_SHIPMENTS}]})
        [unarchive unarchive-status] (use-mutation UNARCHIVE_SHIPMENTS {:refetchQueries [{:query FETCH_ORGANIZATION_SHIPMENTS}]})
        {:keys [shipments]} (some-> data :user :organization)
        selected-shipment-ids (data->selected-row-ids shipments selected-rows)
        on-search-change #(set-search-params
                           (if (empty? %)
                             (dissoc search-params :text)
                             (assoc search-params :text %)))]
    [bare-layout {:title (tr [:view.shipment.list/title])
                  :actions [:div
                            [button {:label [:span {:class "flex items-center"}
                                             [:> CreateIcon {:class "mr-2 w-4 h-4"}]
                                             (tr [:verb/create])]
                                     :class "ml-2 capitalize"
                                     :on-click #(set-state (merge state {:create-open? true}))}]
                            [button {:label [:span {:class "flex items-center"}
                                             [:> UploadIcon {:class "mr-2 w-4 h-4"}]
                                             (tr [:verb/upload])]
                                     :class "ml-2 capitalize"
                                     :on-click #(set-state (merge state {:upload-open? true}))}]]}
     [modal
      {:show (or create-open? false)
       :title (tr [:view.shipment.create/title])
       :on-close #(set-state (merge state {:create-open? false}))}
      [:div {:class "p-4 sm:w-96"}
       [shipment-form {:on-submit #(set-state (merge state {:create-open? false}))}]]]
     [modal
      {:show (or upload-open? false)
       :title (tr [:view.shipment.upload/title])
       :on-close #(set-state (merge state {:upload-open? false}))}
      [:div {:class "w-[80vw] h-[80vh]"}
       [upload-shipments-form {:on-submit #(set-state (merge state {:upload-open? false}))}]]]
     [:div {:class "flex flex-col w-full h-full min-w-0 min-h-0"}
      [:div {:class "flex items-center border-b border-neutral-700 p-4 w-full"}
       [radio-group {:sr-label (tr [:field/status])
                     :value (or status "PENDING")
                     :options [{:key "PENDING" :label (tr [:status/pending])}
                               {:key "ASSIGNED" :label (tr [:status/assigned])}
                               {:key "ARCHIVED" :label (tr [:status/archived])}]
                     :class "mr-2"
                     :on-change #(set-search-params
                                  (if (= % "PENDING")
                                    (dissoc search-params :status)
                                    (assoc search-params :status %)))}]]
      [:div {:class "flex items-center border-b border-neutral-700 p-4 w-full"}
       [input {:icon SearchIcon
               :aria-label (tr [:field/search])
               :placeholder (tr [:field/search])
               :value text
               :class "mr-2"
               :on-text on-search-change}]
       [date-select {:placeholder (tr [:field/date])
                     :value (-> date parse-date d/startOfDay)
                     :class "mr-2"
                     :on-select #(set-search-params
                                  (assoc search-params :date (-> % .getTime)))}]
       (if (= status "ARCHIVED")
         [loading-button {:loading (:loading unarchive-status)
                          :disabled (empty? selected-shipment-ids)
                          :label [:span {:class "flex items-center"}
                                  [:> UnarchiveIcon {:class "mr-2 w-4 h-4"}]
                                  (tr [:verb/unarchive])
                                  (when-not (empty? selected-shipment-ids)
                                    (str " (" (count selected-shipment-ids) ")"))]
                          :class "mr-2 capitalize"
                          :on-click #(do
                                       (unarchive {:variables {:shipmentIds selected-shipment-ids}})
                                       (set-selected-rows #js{}))}]
         [loading-button {:loading (:loading archive-status)
                          :disabled (empty? selected-shipment-ids)
                          :label [:span {:class "flex items-center"}
                                  [:> ArchiveIcon {:class "mr-2 w-4 h-4"}]
                                  (tr [:verb/archive])
                                  (when-not (empty? selected-shipment-ids)
                                    (str " (" (count selected-shipment-ids) ")"))]
                          :class "mr-2 capitalize"
                          :on-click #(do
                                       (archive {:variables {:shipmentIds selected-shipment-ids}})
                                       (set-selected-rows #js{}))}])]
      [:div {:class "w-full h-full min-w-0 min-h-0 overflow-auto"}
       [shipment-table
        {:shipments shipments
         :search-term text
         :set-search-term on-search-change
         :selected-rows selected-rows
         :set-selected-rows set-selected-rows}]
       (if loading
         [:p {:class "p-4 text-center"} (tr [:misc/loading]) "..."]
         (when (empty? shipments)
           [:p {:class "p-4 text-center"} (tr [:misc/empty-search])]))]]]))
