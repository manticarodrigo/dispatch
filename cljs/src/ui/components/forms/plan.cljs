(ns ui.components.forms.plan
  (:require ["react" :refer (useState)]
            ["react-feather" :rename {Truck VehicleIcon
                                      Package ShipmentIcon}]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date half-hours->date)]
            [ui.lib.apollo :refer (gql use-query use-mutation parse-anoms)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.errors :refer (errors)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.components.inputs.range-slider :refer (range-slider)]
            [ui.components.inputs.popover-button :refer (popover-button)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.table :refer (data->selected-row-ids)]
            [ui.components.tables.vehicle :refer (vehicle-table)]
            [ui.components.tables.shipment :refer (shipment-table)]))

(def FETCH_PLAN_OPTIONS (gql (inline "queries/user/organization/fetch-plan-options.graphql")))
(def CREATE_PLAN (gql (inline "mutations/plan/create.graphql")))

(defn plan-form [{:keys [on-submit]}]
  (let [[state set-state] (useState {:windows [[8 36]]
                                     :breaks [[24 26]]})
        [anoms set-anoms] (useState nil)
        [selected-vehicle-rows set-selected-vehicle-rows] (useState #js{})
        [selected-shipment-rows set-selected-shipment-rows] (useState #js{})
        {:keys [depot-id date windows breaks]} state
        {:keys [data]} (use-query FETCH_PLAN_OPTIONS {:variables
                                                      {:filters
                                                       {:start (-> date parse-date d/startOfDay)
                                                        :end (-> date parse-date d/endOfDay)}}})
        [create create-status] (use-mutation CREATE_PLAN {:refetchQueries ["OrganizationPlans"]})
        {:keys [places vehicles shipments]} (-> data :user :organization)
        {:keys [loading]} create-status
        [window] windows
        last-break (last breaks)
        [_ last-end] last-break
        available-break (when (< last-end 46)
                          [(if last-end (+ last-end 1) 0) 48])]
    [:form {:class "flex flex-col"
            :on-submit (fn [e]
                         (.preventDefault e)
                         (-> (create {:variables
                                      {:depotId depot-id
                                       :startAt (->> window first (half-hours->date date))
                                       :endAt (->> window second (half-hours->date date))
                                       :breaks (->> state
                                                    :breaks
                                                    (mapv (fn [[start end]]
                                                            {:startAt (half-hours->date date start)
                                                             :endAt (half-hours->date date end)})))
                                       :vehicleIds (data->selected-row-ids vehicles selected-vehicle-rows)
                                       :shipmentIds (data->selected-row-ids shipments selected-shipment-rows)}})
                             (.then on-submit)
                             (.catch #(set-anoms (parse-anoms %)))))}
     [combobox {:label (tr [:field/depot])
                :value depot-id
                :required true
                :class "mb-4"
                :options places
                :option-to-label #(:name %)
                :option-to-value #(:id %)
                :on-change #(set-state (assoc state :depot-id %))}]
     [date-select {:label (tr [:field/plan-date])
                   :placeholder (tr [:field/date])
                   :value date
                   :required true
                   :class "mb-4"
                   :on-select #(set-state (assoc state :date %))}]
     (when date
       [:<>
        [popover-button
         {:icon VehicleIcon
          :label (tr [:field/vehicles])
          :placeholder (tr [:field/select-vehicles])
          :value (js/Object.keys selected-vehicle-rows)
          :value-to-label #(when (> (count %) 0)
                             (tr [:field/selected-vehicles] [(count %)]))
          :required true
          :class "mb-4"}
         [vehicle-table
          {:vehicles vehicles
           :selected-rows selected-vehicle-rows
           :set-selected-rows set-selected-vehicle-rows}]]
        [popover-button
         {:icon ShipmentIcon
          :label (tr [:field/shipments])
          :placeholder (tr [:field/select-shipments])
          :value (js/Object.keys selected-shipment-rows)
          :value-to-label #(when (> (count %) 0)
                             (tr [:field/selected-shipments] [(count %)]))
          :required true
          :class "mb-4"}
         [shipment-table
          {:shipments shipments
           :selected-rows selected-shipment-rows
           :set-selected-rows set-selected-shipment-rows}]]])
     [range-slider
      {:label (tr [:field/plan-window])
       :min 0
       :max 48
       :step 1
       :ranges windows
       :value-to-label #(str (-> % (/ 2) int) (if (odd? %) ":30" ":00"))
       :available-range available-break
       :add-disabled true
       :remove-disabled (constantly true)
       :on-change #(set-state (assoc state :windows %))
       :on-add #(set-state (assoc state :windows %))
       :on-remove #(set-state (assoc state :windows %))}]
     [range-slider
      {:label (tr [:field/breaks])
       :min 0
       :max 48
       :step 1
       :ranges breaks
       :value-to-label #(str (-> % (/ 2) int) (if (odd? %) ":30" ":00"))
       :available-range available-break
       :on-change #(set-state (assoc state :breaks %))
       :on-add #(set-state (assoc state :breaks %))
       :on-remove #(set-state (assoc state :breaks %))}]
     [submit-button {:loading loading}]
     [errors anoms]]))
