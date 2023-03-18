(ns ui.views.organization.vehicle.list
  (:require [react :refer (useState)]
            [react-feather :rename {Upload UploadIcon
                                    Trash DeleteIcon
                                    Edit CreateIcon}]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.tables.vehicle :refer (vehicle-table)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.loading-button :refer (loading-button)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.forms.vehicle :refer (vehicle-form)]
            [ui.components.forms.upload-vehicles :refer (upload-vehicles-form)]))

(def FETCH_ORGANIZATION_VEHICLES (gql (inline "queries/user/organization/fetch-vehicles.graphql")))
(def ARCHIVE_VEHICLES (gql (inline "mutations/vehicle/archive-vehicles.graphql")))

(defn view []
  (let [[state set-state] (useState {})
        [selected-rows set-selected-rows] (useState #js{})
        {:keys [create-open? upload-open? search-term]} state
        {:keys [data loading]} (use-query FETCH_ORGANIZATION_VEHICLES {})
        [archive archive-status] (use-mutation ARCHIVE_VEHICLES {:refetchQueries [{:query FETCH_ORGANIZATION_VEHICLES}]})
        {:keys [vehicles]} (some-> data :user :organization)
        selected-vehicle-ids (->> vehicles
                                  (map-indexed vector)
                                  (filter
                                   (fn [[idx]]
                                     (= true (aget selected-rows idx))))
                                  (mapv (fn [[_ {:keys [id]}]] id)))]
    [bare-layout {:title (tr [:view.vehicle.list/title])
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
       :title (tr [:view.vehicle.create/title])
       :on-close #(set-state (merge state {:create-open? false}))}
      [:div {:class "p-4 sm:w-96"}
       [vehicle-form {:on-submit #(set-state (merge state {:create-open? false}))}]]]
     [modal
      {:show (or upload-open? false)
       :title (tr [:view.vehicle.upload/title])
       :on-close #(set-state (merge state {:upload-open? false}))}
      [:div {:class "w-[80vw] h-[80vh]"}
       [upload-vehicles-form {:on-submit #(set-state (merge state {:upload-open? false}))}]]]
     (if loading
       [:div {:class "p-4"}
        (str (tr [:misc/loading]) "...")]
       (if vehicles
         [:div {:class "flex flex-col w-full h-full"}
          [:div {:class "flex border-b border-neutral-700 py-2 px-4 w-full"}
           [input {:aria-label (tr [:field/search])
                   :placeholder (tr [:field/search])
                   :value search-term
                   :class "mr-2"
                   :on-text #(set-state (merge state {:search-term %}))}]
           [loading-button {:loading (:loading archive-status)
                            :disabled (empty? selected-vehicle-ids)
                            :label [:span {:class "flex items-center"}
                                    [:> DeleteIcon {:class "mr-2 w-4 h-4"}]
                                    (tr [:verb/archive])
                                    (when-not (empty? selected-vehicle-ids)
                                      (str " (" (count selected-vehicle-ids) ")"))]
                            :class "mr-2 capitalize"
                            :on-click #(do
                                         (archive {:variables {:vehicleIds selected-vehicle-ids}})
                                         (set-selected-rows #js{}))}]]
          [:div {:class "w-full h-full overflow-auto min-w-0"}
           [vehicle-table
            {:data vehicles
             :search-term search-term
             :set-search-term #(set-state (merge state {:search-term %}))
             :selected-rows selected-rows
             :set-selected-rows set-selected-rows}]]]
         [:div {:class "p-4"}
          (tr [:misc/empty-search])]))]))
