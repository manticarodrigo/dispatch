(ns app.organization.layout
  (:require ["react-feather" :rename {BarChart2 AnalyticsIcon
                                      Clipboard TaskIcon
                                      User AgentIcon
                                      MapPin PlaceIcon
                                      Package ShipmentIcon
                                      Navigation PlanIcon
                                      Truck VehicleIcon}]
            [clojure.string :as s]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.nav :refer (nav)]
            [reagent.core :as r]))

(r/set-default-compiler! (r/create-compiler {:function-components true}))

(def nav-items
  [["analytics" :view.analytics/title AnalyticsIcon]
   {:label (s/capitalize (tr [:noun/monitoring]))
    :items [["/organization/agents" :view.agent.list/title AgentIcon]
            ["/organization/places" :view.place.list/title PlaceIcon]
            ["/organization/tasks" :view.task.list/title TaskIcon]]}
   {:label (s/capitalize (tr [:noun/planning]))
    :items [["/organization/vehicles" :view.vehicle.list/title VehicleIcon]
            ["/organization/shipments" :view.shipment.list/title ShipmentIcon]
            ["/organization/plans" :view.plan.list/title PlanIcon]]}])

(def menu-items
  [[{:label  (tr [:view.subscription/title]) :to "subscription/payment"}]
   {:label (str (tr [:misc/sign-out]) "...") :to "/logout"}])

(defn layout [& children]
  [nav
   {:nav-items nav-items
    :menu-items menu-items}
   (into [:<>] children)])

(def ^:export Layout (r/reactify-component layout))
