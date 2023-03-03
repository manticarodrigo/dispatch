(ns ui.views.organization.layout
  (:require [ui.components.layout.nav :refer (nav nav-item)]
            [react-feather :rename {Settings SettingsIcon
                                    Clipboard TaskIcon
                                    User AgentIcon
                                    MapPin PlaceIcon
                                    Package ShipmentIcon
                                    Navigation PlanIcon
                                    Truck VehicleIcon}]

            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.lib.router :refer (use-routes)]
            [ui.components.inputs.back-button :refer (back-button)]
            [ui.components.icons.dispatch :rename {dispatch dispatch-icon}]
            [ui.components.inputs.menu :rename {menu menu-input}]
            [ui.components.inputs.language-radio-group :refer (language-radio-group)]))

(def nav-items [["agents" :view.agent.list/title AgentIcon]
                ["places" :view.place.list/title PlaceIcon]
                ["vehicles" :view.vehicle.list/title VehicleIcon]
                ["shipments" :view.shipment.list/title ShipmentIcon]
                ["plans" :view.plan.list/title PlanIcon]
                ["tasks" :view.task.list/title TaskIcon]])

(def index-routes (mapv
                   (fn [[path]]
                     {:path path
                      :element [dispatch-icon {:class "w-4 h-4"}]})
                   nav-items))

(def routes (conj index-routes {:path "*" :element [back-button]}))

(defn button []
  (use-routes routes))

(defn layout [& children]
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
         :items [[{:label  (tr [:view.subscription/title]) :to "subscription/payment"}]
                 {:label (str (tr [:misc/sign-out]) "...") :to "/logout"}]
         :class-map {:button! "h-full"
                     :item "min-w-[12rem]"}}]]
      [:div {:class "py-2 px-4"}
       [:ul (doall
             (for [[path label icon] nav-items]
               ^{:key path}
               [nav-item path (tr [label]) icon]))]]]
     [:div {:class "py-2 px-4"}
      [language-radio-group]]]]
   (into [:<>] children)])
