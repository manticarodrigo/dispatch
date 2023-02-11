(ns ui.components.forms.place
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.string :refer (class-names)]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.lib.google.maps.autocomplete :refer (search-places)]
            [ui.lib.google.maps.places :refer (find-place)]
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.button :refer (button)]))

(def CREATE_PLACE (gql (inline "mutations/place/create.graphql")))

(defn place-form []
  (let [!state (r/atom {})
        !anoms (r/atom {})
        !options (r/atom [])]
    (fn []
      (let [[create status] (use-mutation CREATE_PLACE {})
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
                              (-> (create {:variables (dissoc @!state :place_id)})
                                  (.then (fn [] (navigate "../places")))
                                  (.catch #(reset! !anoms (parse-anoms %)))))}
          [input {:id "name"
                  :label "Name"
                  :value name
                  :required true
                  :class "pb-4"
                  :on-text #(swap! !state assoc :name %)}]
          [combobox {:label "Location"
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
                  :label "Phone"
                  :value phone
                  :required false
                  :class "pb-4"
                  :on-text #(swap! !state assoc :phone %)}]

          [input {:id "email"
                  :type "email"
                  :label "Email"
                  :value email
                  :required false
                  :class "pb-4"
                  :on-text #(swap! !state assoc :email %)}]

          [button
           {:label (if loading
                     [:span {:class "flex justify-center items-center"}
                      [spinner {:class "mr-2 w-5 h-5"}] "Loading..."]
                     "Submit")
            :class (class-names "my-4 w-full" (when loading "cursor-progress"))
            :disabled loading}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
