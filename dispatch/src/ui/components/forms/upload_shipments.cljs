(ns ui.components.forms.upload-shipments
  (:require ["react" :refer (useState)]
            ["papaparse" :refer (parse)]
            [cljs-bean.core :refer (->clj ->js)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (military->window)]
            [ui.lib.apollo :refer (gql use-mutation)]
            [ui.components.tables.shipment :refer (shipment-table)]
            [ui.components.inputs.dropzone :refer (dropzone)]
            [ui.components.inputs.submit-button :refer (submit-button)]))

(def CREATE_SHIPMENTS (gql (inline "mutations/shipment/create-shipments.graphql")))

(defn upload-shipments-form [{:keys [on-submit]}]
  (let [[shipments set-shipments] (useState nil)
        [selected-rows set-selected-rows] (useState #js{})
        [create create-status] (use-mutation CREATE_SHIPMENTS {:refetchQueries ["OrganizationShipments"]})
        selected-shipments (->> shipments
                                (map-indexed vector)
                                (filter
                                 (fn [[idx]]
                                   (= true (aget selected-rows idx))))
                                (mapv second))]
    (if shipments
      [:form {:class "flex flex-col w-full h-full"
              :on-submit (fn [e]
                           (.preventDefault e)
                           (-> (create
                                {:variables
                                 {:shipments selected-shipments}})
                               (.then on-submit)))}
       [:div {:class "overflow-auto w-full h-full min-w-0 min-h-0"}
        [shipment-table
         {:shipments (mapv #(assoc-in % [:place :name] (:externalPlaceId %)) shipments)
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
                                                           0 "externalId"
                                                           1 "externalPlaceId"
                                                           2 "weight"
                                                           3 "volume"
                                                           4 "duration"
                                                           5 "start1"
                                                           6 "end1"
                                                           7 "start2"
                                                           8 "end2"
                                                           name))
                                      :complete #(do (set-shipments (->> %
                                                                         .-data
                                                                         ->clj
                                                                         (mapv (fn [{:keys [externalId
                                                                                            externalPlaceId
                                                                                            weight
                                                                                            volume
                                                                                            duration
                                                                                            start1
                                                                                            end1
                                                                                            start2
                                                                                            end2]}]
                                                                                 {:externalId externalId
                                                                                  :externalPlaceId externalPlaceId
                                                                                  :weight (js/parseFloat weight)
                                                                                  :volume (js/parseFloat volume)
                                                                                  :duration (js/parseInt duration)
                                                                                  :windows (filter some? [(military->window start1 end1)
                                                                                                          (military->window start2 end2)])}))))
                                                     (set-selected-rows (->> %
                                                                             .-data
                                                                             .-length
                                                                             range
                                                                             (reduce (fn [m i] (assoc m i true)) {})
                                                                             ->js)))})))}]])))
