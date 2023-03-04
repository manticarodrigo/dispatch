(ns ui.views.organization.layout
  (:require [react-feather :rename {Clipboard TaskIcon
                                    User AgentIcon
                                    MapPin PlaceIcon
                                    Package ShipmentIcon
                                    Navigation PlanIcon
                                    Truck VehicleIcon}]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.nav :refer (nav)]))

(def nav-items
  [["agents" :view.agent.list/title AgentIcon]
   ["places" :view.place.list/title PlaceIcon]
   ["vehicles" :view.vehicle.list/title VehicleIcon]
   ["shipments" :view.shipment.list/title ShipmentIcon]
   ["plans" :view.plan.list/title PlanIcon]
   ["task" :view.task.list/title TaskIcon]])

(def menu-items
  [[{:label  (tr [:view.subscription/title]) :to "subscription/payment"}]
   {:label (str (tr [:misc/sign-out]) "...") :to "/logout"}])

(defn layout [& children]
  [nav
   {:nav-items nav-items
    :menu-items menu-items}
   (into [:<>] children)])
