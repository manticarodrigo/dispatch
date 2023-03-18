(ns ui.views.organization.vehicle.list
  (:require [react :refer (useState)]
            [react-feather :rename {Upload UploadIcon
                                    Trash DeleteIcon}]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.tables.vehicle :refer (vehicle-table)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.loading-button :refer (loading-button)]
            [ui.components.forms.upload-vehicles :refer (upload-vehicles-form)]))

(def FETCH_ORGANIZATION_VEHICLES (gql (inline "queries/user/organization/fetch-vehicles.graphql")))
(def ARCHIVE_VEHICLES (gql (inline "mutations/vehicle/archive-vehicles.graphql")))

(defn view []
  (let [[state set-state] (useState {})
        [selected-rows set-selected-rows] (useState #js{})
        {:keys [upload-open?]} state
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
                            [loading-button {:loading (:loading archive-status)
                                             :disabled (empty? selected-vehicle-ids)
                                             :label [:span {:class "flex items-center"}
                                                     [:> DeleteIcon {:class "mr-2 w-4 h-4"}]
                                                     (tr [:verb/archive])
                                                     (when-not (empty? selected-vehicle-ids)
                                                       (str " (" (count selected-vehicle-ids) ")"))]
                                             :class "ml-2 capitalize"
                                             :on-click #(do
                                                          (archive {:variables {:vehicleIds selected-vehicle-ids}})
                                                          (set-selected-rows #js{}))}]
                            [button {:label [:span {:class "flex items-center"}
                                             [:> UploadIcon {:class "mr-2 w-4 h-4"}]
                                             (tr [:verb/upload])]
                                     :class "ml-2 capitalize"
                                     :on-click #(set-state (merge state {:upload-open? true}))}]]}
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
         [:div {:class "w-full h-full overflow-auto"}
          [vehicle-table
           {:data vehicles
            :selected-rows selected-rows
            :set-selected-rows set-selected-rows}]]
         [:div {:class "p-4"}
          (tr [:misc/empty-search])]))]))
