(ns ui.views.seat.layout
  (:require [react]
            [shadow.resource :refer (inline)]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.lib.platform :refer (platform)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.input :refer (debounce)]
            [ui.hooks.use-location :refer (use-location)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.button :refer (button)]))

(def CREATE_DEVICE (gql (inline "mutations/device/create.graphql")))
(def CREATE_LOCATION (gql (inline "mutations/location/create.graphql")))

(def -debounce
  (debounce
   (fn [fn]
     (fn))
   500))

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
        (fn [position]
          (dispatch [:device/position
                     {:title "Your device"
                      :position position}])
          (-debounce
           #(create-location
             {:variables {:seatId seat-id
                          :deviceId device-id
                          :position position}}))))
       #())
     #js[])

    [:<>
     (if (= platform "web")
       [modal {:show true :title (tr [:device.unsupported/title]) :on-close #()}
        [:p {:class "mb-4"} (tr [:device.unsupported/message])]
        [:a {:href "https://play.google.com/store/apps/details?id=app.ambito.dispatch"
             :class "underline"}
         (tr [:device.unsupported/download])]]
       [:<>
        [modal {:show invalid? :title (tr [:device.linked/title]) :on-close #()}
         [:p {:class "mb-4"} (tr [:device.linked/message])]]
        [modal {:show unlinked? :title (tr [:device.unlinked/title]) :on-close #()}
         [:p {:class "mb-4"} (tr [:device.unlinked/message])]
         [button {:label (tr [:device/link])
                  :on-click (fn []
                              (-> (create-device {:variables
                                                  {:seatId seat-id
                                                   :deviceId device-id
                                                   :info device-info}})
                                  (.then #(dispatch [:device/error nil]))))}]]])
     (into [:<>] children)]))
