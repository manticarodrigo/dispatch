(ns ui.views.seat.layout
  (:require [react]
            [clojure.set :refer (rename-keys)]
            [shadow.resource :refer (inline)]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.lib.platform :refer (platform)]
            [ui.hooks.use-location :refer (use-location)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.button :refer (button)]))

(def CREATE_DEVICE (gql (inline "mutations/device/create.graphql")))
(def CREATE_LOCATION (gql (inline "mutations/location/create.graphql")))

(defn layout [& children]
  (let [{seat-id :seat} (use-params)
        watch-location (use-location)
        [create-device] (use-mutation CREATE_DEVICE {:refetchQueries ["SeatByDevice"]})
        [create-location] (use-mutation CREATE_LOCATION {})
        device (listen [:device])
        device-id (:id device)
        device-info (:info device)
        device-error (:error device)
        unlinked? (= "device-not-linked" device-error)
        invalid? (= "invalid-token" device-error)]

    (react/useEffect
     (fn []
       (watch-location
        (fn [location]
          (dispatch [:map {:locations [{:title "Your device"
                                        :position (rename-keys {:latitude :lat :longitude :lng} location)}]}])
          (create-location {:variables (merge {:seatId seat-id} location)})))
       #())
     #js[])

    [:<>
     (if (= platform "web")
       [modal {:show true :title "Unsupported platform" :on-close #()}
        [:p {:class "mb-4"} "Looks like you are trying to access a seat view from a web browser. Please use the mobile app to access this view."]
        [:a {:href "https://play.google.com/store/apps/details?id=app.ambito.dispatch"
             :class "underline"}
         "Download from Google Play Store"]]
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
                                  (.then #(dispatch [:device/error nil]))))}]]])
     (into [:<>] children)]))
