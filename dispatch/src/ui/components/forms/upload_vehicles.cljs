(ns ui.components.forms.upload-vehicles
  (:require ["react" :refer (useState)]
            ["papaparse" :refer (parse)]
            [cljs-bean.core :refer (->clj ->js)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-mutation)]
            [ui.components.tables.vehicle :refer (vehicle-table)]
            [ui.components.inputs.dropzone :refer (dropzone)]
            [ui.components.inputs.submit-button :refer (submit-button)]))

(def CREATE_VEHICLES (gql (inline "mutations/vehicle/create-vehicles.graphql")))

(defn upload-vehicles-form [{:keys [on-submit]}]
  (let [[vehicles set-vehicles] (useState nil)
        [selected-rows set-selected-rows] (useState #js{})
        [create create-status] (use-mutation CREATE_VEHICLES {:refetchQueries ["OrganizationVehicles"]})
        selected-vehicles (->> vehicles
                               (map-indexed vector)
                               (filter
                                (fn [[idx]]
                                  (= true (aget selected-rows idx))))
                               (mapv second))]
    (if vehicles
      [:form {:class "flex flex-col w-full h-full"
              :on-submit (fn [e]
                           (.preventDefault e)
                           (-> (create
                                {:variables
                                 {:vehicles selected-vehicles}})
                               (.then on-submit)))}
       [:div {:class "overflow-auto w-full h-full min-w-0 min-h-0"}
        [vehicle-table
         {:data vehicles
          :selected-rows selected-rows
          :set-selected-rows set-selected-rows}]]
       [:div {:class "p-4"}
        [submit-button {:loading (:loading create-status)}]]]
      [:div {:class "flex flex-col items-center justify-center p-8 w-full h-full"}
       [dropzone
        {:accept #js{"text/csv" #js[".csv"]}
         :on-drop (fn [files]
                    (when-let [file (first files)]
                      (parse file #js{:header true
                                      :transformHeader (fn [name idx]
                                                         (case idx
                                                           0 "name"
                                                           1 "weight"
                                                           2 "volume"
                                                           name))
                                      :complete #(do (set-vehicles (->> %
                                                                        .-data
                                                                        ->clj
                                                                        (mapv (fn [{:keys [name weight volume]}]
                                                                                {:name name
                                                                                 :weight (js/parseFloat weight)
                                                                                 :volume (js/parseFloat volume)}))))
                                                     (set-selected-rows (->> %
                                                                             .-data
                                                                             .-length
                                                                             range
                                                                             (reduce (fn [m i] (assoc m i true)) {})
                                                                             ->js)))})))}]])))
