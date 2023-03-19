(ns ui.views.organization.shipment.list
  (:require ["react" :refer (useState)]
            ["react-feather" :rename {Upload UploadIcon
                                      Trash DeleteIcon
                                      Edit CreateIcon
                                      Search SearchIcon}]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.tables.shipment :refer (shipment-table)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.loading-button :refer (loading-button)]))

(def FETCH_ORGANIZATION_SHIPMENTS (gql (inline "queries/user/organization/fetch-shipments.graphql")))

(defn view []
  (let [[state set-state] (useState {})
        [selected-rows set-selected-rows] (useState #js{})
        {:keys [create-open? upload-open? search-term]} state
        {:keys [data loading]} (use-query FETCH_ORGANIZATION_SHIPMENTS {})
        {:keys [shipments]} (some-> data :user :organization)
        selected-shipment-ids (->> shipments
                                   (map-indexed vector)
                                   (filter
                                    (fn [[idx]]
                                      (= true (aget selected-rows idx))))
                                   (mapv (fn [[_ {:keys [id]}]] id)))]
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
     (if loading
       [:div {:class "p-4"}
        (tr [:misc/loading]) "..."]
       [:div {:class "flex flex-col w-full h-full min-w-0 min-h-0"}
        [:div {:class "flex border-b border-neutral-700 p-4 w-full"}
         [input {:icon SearchIcon
                 :aria-label (tr [:field/search])
                 :placeholder (tr [:field/search])
                 :value search-term
                 :class "mr-2"
                 :on-text #(set-state (merge state {:search-term %}))}]
         [loading-button {
                          ;; :loading (:loading archive-status)
                          :disabled (empty? selected-shipment-ids)
                          :label [:span {:class "flex items-center"}
                                  [:> DeleteIcon {:class "mr-2 w-4 h-4"}]
                                  (tr [:verb/archive])
                                  (when-not (empty? selected-shipment-ids)
                                    (str " (" (count selected-shipment-ids) ")"))]
                          :class "mr-2 capitalize"
                          ;; :on-click #(do
                          ;;              (archive {:variables {:shipmentIds selected-shipment-ids}})
                          ;;              (set-selected-rows #js{}))
                          }]]
        [:div {:class "w-full h-full min-w-0 min-h-0 overflow-auto"}
         [shipment-table
          {:shipments shipments
           :search-term search-term
           :set-search-term #(set-state (merge state {:search-term %}))
           :selected-rows selected-rows
           :set-selected-rows set-selected-rows}]]])]))
