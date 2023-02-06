(ns ui.views.seat.layout
  (:require ["@apollo/client" :refer (gql)]
            [shadow.resource :refer (inline)]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.button :refer (button)]))

(def LINK_DEVICE (gql (inline "mutations/device/link.graphql")))

(defn layout [& children]
  (let [{seat-id :id} (use-params)
        device (listen [:device])
        device-id (:id device)
        device-info (:info device)
        device-error (:error device)
        [link-device] (use-mutation LINK_DEVICE {:refetchQueries ["SeatByDevice"]})
        unlinked? (= "device-not-linked" device-error)
        invalid? (= "invalid-token" device-error)]
    [:<>
     [modal {:show invalid? :title "Another device already linked" :on-close #()}
      [:p {:class "mb-4"} "Looks like this seat has a device linked to it already. If you would like to link your device to this seat, please reach out to an admin and ask them to unlink the other device first."]]
     [modal {:show unlinked? :title "No device linked" :on-close #()}
      [:p {:class "mb-4"} "Looks like this seat has no device linked to it yet. Please press the button below to link your device and continue."]
      [button {:label "Link Device"
               :class "bg-neutral-900 text-white px-4 py-2 rounded-md"
               :on-click (fn []
                           (-> (link-device {:variables {:seatId seat-id
                                                         :token device-id
                                                         :info device-info}})
                               (.then #(dispatch [:device/error nil]))))}]]
     (into [:<>] children)]))
