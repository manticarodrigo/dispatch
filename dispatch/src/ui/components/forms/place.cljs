(ns ui.components.forms.place
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-params use-navigate)]
            [ui.lib.google.maps.autocomplete :refer (search-places)]
            [ui.lib.google.maps.places :refer (find-place)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def CREATE_PLACE (gql (inline "mutations/place/create.graphql")))

(defn place-form []
  (let [!state (r/atom {})
        !anoms (r/atom {})
        !options (r/atom [])]
    (fn []
      (let [{agent-id :agent} (use-params)
            device (listen [:device])
            device-id (:id device)
            [create status] (use-mutation CREATE_PLACE {})
            {:keys [name phone email place_id]} @!state
            {:keys [loading]} status
            navigate (use-navigate)
            selected-place-missing? (and
                                     place_id
                                     (not-any?
                                      #(= (:place_id %) place_id)
                                      @!options))
            place-options (if selected-place-missing?
                            (cons @!state @!options)
                            @!options)]
        [:<>
         (when (and (-> @!state :lat)
                    (-> @!state :lng))
           [:p {:class "sr-only"} "found coordinates"])

         [:form {:class "flex flex-col"
                 :on-submit (fn [e]
                              (.preventDefault e)
                              (let [variables (dissoc @!state :place_id)]
                                (-> (create {:variables (if agent-id
                                                          (assoc variables
                                                                 :agentId agent-id
                                                                 :deviceId device-id)
                                                          variables)})
                                    (.then (fn [] (navigate "../places")))
                                    (.catch #(reset! !anoms (parse-anoms %))))))}
          [input {:id "name"
                  :label (tr [:field/name])
                  :value name
                  :required true
                  :class "pb-4"
                  :on-text #(swap! !state assoc :name %)}]
          [combobox {:label (tr [:field/location])
                     :class "pb-4"
                     :value place_id
                     :options place-options
                     :option-to-label #(:description %)
                     :option-to-value #(:place_id %)
                     :on-text (fn [text]
                                (when (seq text)
                                  (-> (search-places text)
                                      (.then #(reset! !options %)))))
                     :on-select (fn [option]
                                  (reset! !state
                                          (merge {:name name}
                                                 (select-keys option [:place_id :description])))

                                  (-> (find-place (:place_id option))
                                      (.then #(merge % option))
                                      (.then #(swap! !state merge (select-keys % [:place_id :description :lat :lng])))))}]
          [input {:id "phone"
                  :type "tel"
                  :label (tr [:field/phone])
                  :value phone
                  :required false
                  :class "pb-4"
                  :on-text #(swap! !state assoc :phone %)}]
          [input {:id "email"
                  :type "email"
                  :label (tr [:field/email])
                  :value email
                  :required false
                  :class "pb-4"
                  :on-text #(swap! !state assoc :email %)}]
          [submit-button {:loading loading}]
          [errors @!anoms]]]))))
