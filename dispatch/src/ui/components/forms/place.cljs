(ns ui.components.forms.place
  (:require [react]
            [clojure.set :refer (rename-keys)]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-params use-navigate)]
            [ui.lib.location :refer (get-location)]
            [ui.lib.google.maps.autocomplete :refer (search-places)]
            [ui.lib.google.maps.places :refer (find-place)]
            [ui.lib.google.maps.geocoding :refer (reverse-geocode)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.location :refer (position-to-lat-lng)]
            [ui.utils.validation :refer (latitude? longitude?)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.loading-button :refer (loading-button)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def CREATE_PLACE (gql (inline "mutations/place/create.graphql")))

(defn place-form []
  (let [!state (r/atom {})
        !anoms (r/atom {})
        !options (r/atom [])
        !loading-place (r/atom false)
        !loading-location (r/atom false)]
    (fn []
      (let [{agent-id :agent} (use-params)
            device (listen [:device])
            device-id (:id device)
            [create status] (use-mutation CREATE_PLACE {})
            {:keys [name description lat lng place_id phone email]} @!state
            loading (or
                     (:loading status)
                     @!loading-place
                     @!loading-location)
            navigate (use-navigate)
            selected-place-missing? (and
                                     place_id
                                     (not-any?
                                      #(= (:place_id %) place_id)
                                      @!options))
            place-options (if selected-place-missing?
                            (cons @!state @!options)
                            @!options)]

        (react/useEffect
         (fn []
           (dispatch [:map
                      {:points
                       (when (and lat lng)
                         [{:title (or name "???")
                           :position {:lat lat :lng lng}}])}])
           #())
         #js[lat lng])

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
         [input {:label (tr [:field/name])
                 :value name
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :name %)}]
         [combobox {:label (tr [:field/location-search])
                    :class "mb-4"
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
                                 (reset! !loading-place true)
                                 (-> (find-place (:place_id option))
                                     (.then #(merge % option))
                                     (.then #(select-keys % [:place_id :description :lat :lng]))
                                     (.then #(swap! !state merge %))
                                     (.then #(reset! !loading-place false))
                                     (.catch #(reset! !loading-place false))))}]
         [loading-button
          {:loading @!loading-location
           :type "button"
           :label (tr [:field/location-get])
           :class "mb-4"
           :on-click (fn []
                       (reset! !loading-location true)
                       (-> (get-location)
                           (.then (fn [position]
                                    (let [lat-lng (position-to-lat-lng position)]
                                      (swap! !state merge lat-lng)
                                      (-> (reverse-geocode lat-lng)
                                          (.then #(-> % :results first))
                                          (.then #(select-keys % [:place_id :formatted_address]))
                                          (.then #(rename-keys % {:formatted_address :description}))
                                          (.then #(swap! !state merge %))
                                          (.then #(reset! !loading-location false))))))
                           (.catch #(reset! !loading-location false))))}]
         [input {:label (tr [:field/description])
                 :value description
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :description %)}]
         [input {:label (tr [:field/latitude])
                 :value lat
                 :required true
                 :class "mb-4"
                 :on-validate latitude?
                 :on-text #(swap! !state assoc :lat %)}]
         [input {:label (tr [:field/longitude])
                 :value lng
                 :required true
                 :class "mb-4"
                 :on-validate longitude?
                 :on-text #(swap! !state assoc :lng %)}]
         [input {:type "tel"
                 :label (tr [:field/phone])
                 :value phone
                 :required false
                 :class "mb-4"
                 :on-text #(swap! !state assoc :phone %)}]
         [input {:type "email"
                 :label (tr [:field/email])
                 :value email
                 :required false
                 :class "mb-4"
                 :on-text #(swap! !state assoc :email %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
