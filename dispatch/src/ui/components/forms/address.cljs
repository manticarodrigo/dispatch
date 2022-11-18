(ns ui.components.forms.address
  (:require ["@apollo/client" :refer (gql useMutation)]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->js)]
            [ui.utils.error :refer (tr-error)]
            [ui.lib.apollo :refer (parse-anoms)]
            [ui.lib.router :refer (use-navigate)]
            [ui.lib.google.maps.autocomplete :refer (search-places)]
            [ui.lib.google.maps.places :refer (find-place)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.combobox :refer (combobox)]
            [ui.components.inputs.generic.button :refer (button)]))

(def FETCH_ADDRESSES (gql (inline "queries/address/fetch-all.graphql")))
(def CREATE_ADDRESS (gql (inline "mutations/address/create.graphql")))

(defn address-form []
  (let [!state (r/atom {})
        !anoms (r/atom {})
        !options (r/atom [])]
    (fn []
      (let [[create] (useMutation
                      CREATE_ADDRESS
                      (->js {:refetchQueries [{:query FETCH_ADDRESSES}]}))
            navigate (use-navigate)]
        [:<>
         (when (and (-> @!state :lat)
                    (-> @!state :lng))
           [:p {:class "sr-only"} "found coordinates"])

         [:form {:class "flex flex-col"
                 :on-submit (fn [e]
                              (.preventDefault e)
                              (-> (create (->js {:variables (dissoc @!state :place_id)}))
                                  (.then (fn [] (navigate "/route")))
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
                                      (.then #(filterv :place_id %))
                                      (.then #(reset! !options %)))))
                     :on-select (fn [option]
                                  (reset! !state
                                          (merge {:name (-> @!state :name)}
                                                 (select-keys option [:place_id :description])))

                                  (-> (find-place (:place_id option))
                                      (.then #(merge % option))
                                      (.then #(swap! !state merge (select-keys % [:place_id :description :lat :lng])))))}]
          [button {:label "Submit" :class "my-4"}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
