(ns ui.components.forms.address
  (:require ["@apollo/client" :refer (gql)]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.string :refer (class-names)]
            [ui.lib.apollo :refer (parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.lib.google.maps.autocomplete :refer (search-places)]
            [ui.lib.google.maps.places :refer (find-place)]
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.combobox :refer (combobox)]
            [ui.components.inputs.generic.button :refer (button)]))

(def CREATE_ADDRESS (gql (inline "mutations/address/create.graphql")))

(defn address-form []
  (let [!state (r/atom {})
        !anoms (r/atom {})
        !options (r/atom [])]
    (fn []
      (let [[create status] (use-mutation CREATE_ADDRESS {})
            {:keys [loading]} status
            navigate (use-navigate)]
        [:<>
         (when (and (-> @!state :lat)
                    (-> @!state :lng))
           [:p {:class "sr-only"} "found coordinates"])

         [:form {:class "flex flex-col"
                 :on-submit (fn [e]
                              (.preventDefault e)
                              (-> (create {:variables (dissoc @!state :place_id)})
                                  (.then (fn [] (navigate "/admin/addresses")))
                                  (.catch #(reset! !anoms (parse-anoms %)))))}
          [input {:id "name"
                  :label "Name"
                  :value (:name @!state)
                  :required true
                  :class "pb-4"
                  :on-text #(swap! !state assoc :name %)}]
          [combobox {:label "Location"
                     :class "pb-4"
                     :value (:place_id @!state)
                     :options (if (and
                                   (:place_id @!state)
                                   (not-any?
                                    #(= (:place_id %) (:place_id @!state))
                                    @!options))
                                (cons @!state @!options)
                                @!options)
                     :option-to-label #(:description %)
                     :option-to-value #(:place_id %)
                     :on-text (fn [text]
                                (when (seq text)
                                  (-> (search-places text)
                                      (.then #(reset! !options %)))))
                     :on-select (fn [option]
                                  (reset! !state
                                          (merge {:name (-> @!state :name)}
                                                 (select-keys option [:place_id :description])))

                                  (-> (find-place (:place_id option))
                                      (.then #(merge % option))
                                      (.then #(swap! !state merge (select-keys % [:place_id :description :lat :lng])))))}]
          [input {:id "phone"
                  :label "Phone"
                  :value (:phone @!state)
                  :required false
                  :class "pb-4"
                  :type "tel"
                  :on-text #(swap! !state assoc :phone %)}]

          [input {:id "email"
                  :label "Email"
                  :value (:email @!state)
                  :required false
                  :class "pb-4"
                  :type "email"
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
