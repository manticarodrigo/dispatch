(ns ui.components.forms.shipment
  (:require ["react" :refer (useState)]
            ["react-feather" :rename {Plus  PlusIcon
                                      Minus MinusIcon}]
            [clojure.string :as s]
            [shadow.resource :refer (inline)]
            [ui.utils.vector :refer (vec-remove)]
            [ui.lib.apollo :refer (gql use-query use-mutation parse-anoms)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.input :refer (input label-class)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.components.inputs.slider :refer (slider)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def FETCH_ORGANIZATION_TASK_OPTIONS (gql (inline "queries/user/organization/fetch-task-options.graphql")))
(def CREATE_SHIPMENTS (gql (inline "mutations/shipment/create-shipments.graphql")))

(defn half-hours-to-date [date half-hours]
  (-> date
      d/startOfDay
      (d/addMinutes (* half-hours 30))))

(defn shipment-form [{:keys [on-submit]}]
  (let [[state set-state] (useState {:windows [[7 24]]})
        [anoms set-anoms] (useState nil)
        {:keys [data]} (use-query FETCH_ORGANIZATION_TASK_OPTIONS {})
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
                                                              {:startAt (half-hours-to-date date start)
                                                               :endAt (half-hours-to-date date end)})))}}})
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
     [:label
      [:span {:class label-class} (tr [:field/visit-windows])]
      (doall
       (for [[idx window] (map-indexed vector windows)]
         (let [first? (= idx 0)]
           ^{:key idx}
           [:div {:class "flex items-center mt-4 mb-8"}
            [slider {:min 0
                     :max 48
                     :step 1
                     :value window
                     :value-to-label #(str (-> % (/ 2) int) (if (odd? %) ":30" ":00"))
                     :on-change (fn [[start end]]
                                  (let [prev-window (get windows (dec idx))
                                        next-window (get windows (inc idx))
                                        [_ prev-end] prev-window
                                        [next-start _] next-window
                                        current-window-valid (> end start)
                                        prev-window-valid (or
                                                           (nil? prev-window)
                                                           (> start prev-end))
                                        next-window-valid (or
                                                           (nil? next-window)
                                                           (< end next-start))]
                                    (when (and current-window-valid
                                               prev-window-valid
                                               next-window-valid)
                                      (set-state (assoc-in state [:windows idx] [start end])))))}]
            [:div {:class "ml-2 w-12"}
             (when-not first?
               [button {:type "button"
                        :label [:> MinusIcon {:class "w-4 h-4"}]
                        :on-click #(set-state (update state :windows vec-remove idx))}])]])))
      [button
       {:type "button"
        :label [:div {:class "flex justify-center items-center"}
                [:> PlusIcon {:class "mr-2 w-4 h-4"}] (tr [:field/add-window])]
        :disabled (not available-window)
        :class (class-names "my-2 border-2 border-dashed w-full")
        :on-click #(set-state (update state :windows conj available-window))}]]
     [submit-button {:loading loading}]
     [errors anoms]]))
