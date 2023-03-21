(ns ui.components.forms.shipment
  (:require ["react" :refer (useState)]
            [clojure.string :as s]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (half-hours->date)]
            [ui.lib.apollo :refer (gql use-query use-mutation parse-anoms)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.components.inputs.range-slider :refer (range-slider)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def FETCH_SHIPMENT_OPTIONS (gql (inline "queries/user/organization/fetch-shipment-options.graphql")))
(def CREATE_SHIPMENTS (gql (inline "mutations/shipment/create-shipments.graphql")))

(defn shipment-form [{:keys [on-submit]}]
  (let [[state set-state] (useState {:windows [[7 24]]})
        [anoms set-anoms] (useState nil)
        {:keys [data]} (use-query FETCH_SHIPMENT_OPTIONS {})
        [create create-status] (use-mutation CREATE_SHIPMENTS {:refetchQueries ["OrganizationShipments"]})
        {:keys [places]} (-> data :user :organization)
        {:keys [loading]} create-status
        {:keys [place-id weight volume duration date windows]} state
        last-window (last windows)
        [_ last-end] last-window
        available-window (when (< last-end 46)
                           [(+ last-end 1) 48])]
    [:form {:class "flex flex-col"
            :on-submit (fn [e]
                         (.preventDefault e)
                         (-> (create {:variables
                                      {:shipments
                                       {:placeId (:place-id state)
                                        :weight (-> state :weight js/parseFloat)
                                        :volume (-> state :volume js/parseFloat)
                                        :duration (-> state :duration js/parseFloat)
                                        :windows (->> state
                                                      :windows
                                                      (mapv (fn [[start end]]
                                                              {:startAt (half-hours->date date start)
                                                               :endAt (half-hours->date date end)})))}}})
                             (.then on-submit)
                             (.catch #(set-anoms (parse-anoms %)))))}
     [combobox {:label (tr [:field/place])
                :value place-id
                :required true
                :class "mb-4"
                :options places
                :option-to-label #(:name %)
                :option-to-value #(:id %)
                :on-change #(set-state (assoc state :place-id %))}]
     [input {:label (tr [:field/load-weight])
             :value weight
             :class "mb-4"
             :on-text #(set-state (assoc state :weight %))}]
     [input {:label (tr [:field/load-volume])
             :value volume
             :class "mb-4"
             :on-text #(set-state (assoc state :volume %))}]
     [input {:label (tr [:field/visit-duration])
             :value duration
             :class "mb-4"
             :on-text #(set-state (assoc state :duration %))}]
     [date-select {:label (s/capitalize (tr [:field/visit-date]))
                   :placeholder (tr [:field/date])
                   :value date
                   :required true
                   :class "mb-4"
                   :on-select #(set-state (assoc state :date %))}]
     [range-slider
      {:label (tr [:field/visit-windows])
       :min 0
       :max 48
       :step 1
       :ranges windows
       :value-to-label #(str (-> % (/ 2) int) (if (odd? %) ":30" ":00"))
       :remove-disabled #(= % 0)
       :available-range available-window
       :on-change #(set-state (assoc state :windows %))
       :on-add #(set-state (assoc state :windows %))
       :on-remove #(set-state (assoc state :windows %))}]
     [submit-button {:loading loading}]
     [errors anoms]]))
