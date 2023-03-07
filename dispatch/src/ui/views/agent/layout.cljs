(ns ui.views.agent.layout
  (:require [react]
            [react-feather :rename {Clipboard TaskIcon
                                    MapPin PlaceIcon}]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-mutation)]
            [ui.lib.platform :refer (platform)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.input :refer (debounce)]
            [ui.hooks.use-location :refer (use-location)]
            [ui.components.layout.nav :refer (nav)]
            [ui.components.modal :refer (modal)]))

(def CREATE_LOCATION (gql (inline "mutations/location/create.graphql")))

(def -debounce
  (debounce
   (fn [fn]
     (fn))
   500))

(def nav-items
  [["tasks" :view.task.list/title TaskIcon]
   ["places" :view.place.list/title PlaceIcon]])

(def menu-items
  [{:label (str (tr [:misc/sign-out]) "...") :to "/logout"}])

(defn layout [& children]
  (let [watch-location (use-location)
        [create-location] (use-mutation CREATE_LOCATION {})
        unsupported? (= platform "")]

    (react/useEffect
     (fn []
       (when-not unsupported?
         (watch-location
          (fn [position]
            (dispatch [:device/position
                       {:title "Your device"
                        :position position}])
            (-debounce
             #(create-location
               {:variables {:position position}})))))
       #())
     #js[])

    [:<>
     (when unsupported?
       [modal {:show true :title (tr [:device.unsupported/title]) :on-close #()}
        [:div {:class "p-4"}
         [:p {:class "mb-4"} (tr [:device.unsupported/message])]
         [:a {:href "https://play.google.com/store/apps/details?id=app.ambito.dispatch"
              :class "underline"}
          (tr [:device.unsupported/download])]]])
     [nav
      {:nav-items nav-items
       :menu-items menu-items}
      (into [:<>] children)]]))
