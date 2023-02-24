(ns ui.views.agent.layout
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
  (let [{agent-id :agent} (use-params)
        watch-location (use-location)
        [create-device] (use-mutation CREATE_DEVICE {})
        [create-location] (use-mutation CREATE_LOCATION {})
        device (listen [:device])
        device-id (:id device)
        device-info (:info device)
        device-error (:error device)]

    (react/useEffect
     (fn []
       (watch-location
        (fn [position]
          (dispatch [:device/position
                     {:title "Your device"
                      :position position}])
          (-debounce
           #(create-location
             {:variables {:agentId agent-id
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
        [modal {:show (= "agent-not-found" device-error) :title (tr [:device.agent-not-found/title]) :on-close #()}
         [:p {:class "mb-4"} (tr [:device.agent-not-found/message])]]
        [modal {:show (= "device-token-invalid" device-error) :title (tr [:device.device-token-invalid/title]) :on-close #()}
         [:p {:class "mb-4"} (tr [:device.device-token-invalid/message])]]
        [modal {:show (= "device-already-linked" device-error) :title (tr [:device.device-already-linked/title]) :on-close #()}
         [:p {:class "mb-4"} (tr [:device.device-already-linked/message])]]
        [modal {:show (= "device-not-linked" device-error) :title (tr [:device.device-not-linked/title]) :on-close #()}
         [:p {:class "mb-4"} (tr [:device.device-not-linked/message])]
         [button {:label (tr [:device/link])
                  :on-click (fn []
                              (-> (create-device {:variables
                                                  {:agentId agent-id
                                                   :deviceId device-id
                                                   :info device-info}})
                                  (.then #(dispatch [:device/error nil]))))}]]])
     (into [:<>] children)]))
