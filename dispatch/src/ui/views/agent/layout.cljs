(ns ui.views.agent.layout
  (:require [react]
            [ui.components.layout.nav :refer (nav nav-item)]
            [react-feather :rename {Settings SettingsIcon
                                    Clipboard TaskIcon
                                    MapPin PlaceIcon}]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.router :refer (use-routes)]
            [ui.lib.apollo :refer (gql use-mutation)]
            [ui.lib.platform :refer (platform)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.input :refer (debounce)]
            [ui.hooks.use-location :refer (use-location)]
            [ui.components.inputs.back-button :refer (back-button)]
            [ui.components.icons.dispatch :rename {dispatch dispatch-icon}]
            [ui.components.inputs.menu :rename {menu menu-input}]
            [ui.components.inputs.language-radio-group :refer (language-radio-group)]
            [ui.components.modal :refer (modal)]))

(def CREATE_LOCATION (gql (inline "mutations/location/create.graphql")))

(def -debounce
  (debounce
   (fn [fn]
     (fn))
   500))

(def nav-items [["tasks" :view.task.list/title TaskIcon]
                ["places" :view.place.list/title PlaceIcon]])

(def index-routes (mapv
                   (fn [[path]]
                     {:path path
                      :element [dispatch-icon {:class "w-4 h-4"}]})
                   nav-items))

(def routes (conj index-routes {:path "*" :element [back-button]}))

(defn button []
  (use-routes routes))

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
        [:p {:class "mb-4"} (tr [:device.unsupported/message])]
        [:a {:href "https://play.google.com/store/apps/details?id=app.ambito.dispatch"
             :class "underline"}
         (tr [:device.unsupported/download])]])
     [:div {:class "flex w-full h-full"}
      [nav
       [:div {:class "flex flex-col justify-between h-full"}
        [:div
         [:div {:class (class-names
                        "py-4 px-4"
                        "flex justify-between items-center"
                        "w-full")}
          [button]
          [menu-input
           {:label [:> SettingsIcon {:class "w-4 h-4"}]
            :items [{:label (str (tr [:misc/sign-out]) "...") :to "/logout"}]
            :class-map {:button! "h-full"
                        :item "min-w-[12rem]"}}]]
         [:div {:class "py-2 px-4"}
          [:ul (doall
                (for [[path label icon] nav-items]
                  ^{:key path}
                  [nav-item path (tr [label]) icon]))]]]
        [:div {:class "py-2 px-4"}
         [language-radio-group]]]]
      (into [:<>] children)]]))

