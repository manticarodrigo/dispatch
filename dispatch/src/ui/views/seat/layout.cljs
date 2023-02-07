(ns ui.views.seat.layout
  (:require [react]
            [shadow.resource :refer (inline)]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.lib.google.maps.overlay :refer (update-overlay)]
            [ui.hooks.use-location :refer (use-location)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.button :refer (button)]))

(def CREATE_DEVICE (gql (inline "mutations/device/create.graphql")))
(def CREATE_LOCATION (gql (inline "mutations/location/create.graphql")))

(defn layout [& children]
  (let [{seat-id :seat} (use-params)
        device (listen [:device])
        device-id (:id device)
        device-info (:info device)
        device-error (:error device)
        unlinked? (= "device-not-linked" device-error)
        invalid? (= "invalid-token" device-error)
        [create-device] (use-mutation CREATE_DEVICE {:refetchQueries ["SeatByDevice"]})
        watch-location (use-location)
        [create-location] (use-mutation CREATE_LOCATION {})]

    (react/useEffect
     (fn []
       (watch-location
        (fn [location]
          (let [lat-lng {:lat (:latitude location)
                         :lng (:longitude location)}]
            (update-overlay lat-lng)
            (create-location {:variables (merge {:seatId seat-id} lat-lng)}))))
       #())
     #js[])

    [:<>
     [modal {:show invalid? :title "Another device already linked" :on-close #()}
      [:p {:class "mb-4"} "Looks like this seat has a device linked to it already. If you would like to link your device to this seat, please reach out to an admin and ask them to unlink the other device first."]]
     [modal {:show unlinked? :title "No device linked" :on-close #()}
      [:p {:class "mb-4"} "Looks like this seat has no device linked to it yet. Please press the button below to link your device and continue."]
      [button {:label "Link Device"
               :class "bg-neutral-900 text-white px-4 py-2 rounded-md"
               :on-click (fn []
                           (-> (create-device {:variables
                                               {:seatId seat-id
                                                :deviceId device-id
                                                :info device-info}})
                               (.then #(dispatch [:device/error nil]))))}]]
     (into [:<>] children)]))
