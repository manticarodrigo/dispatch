(ns ui.components.header.nav
  (:require [react-feather :rename {Clipboard TaskIcon
                                    Users AgentIcon
                                    MapPin PlaceIcon
                                    Package ShipmentIcon
                                    Navigation PlanIcon
                                    Truck VehicleIcon}]
            [ui.lib.router :refer (nav-link use-routes)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]))

(defn nav-item [to label icon]
  [:li
   [nav-link
    {:to to
     :class
     (fn [{:keys [isActive]}]
       (class-names
        "transition"
        "flex items-center"
        "mb-1"
        "rounded"
        "py-1 px-2"
        "text-sm"
        "hover:bg-neutral-700 focus:bg-neutral-700"
        (if isActive
          "text-neutral-300 bg-neutral-800"
          "text-neutral-400 hover:text-neutral-50 focus:text-neutral-50")))}
    [:> icon {:class "w-4 h-4"}]
    [:span {:class "ml-2"} label]]])

(defn nav []
  (use-routes [{:path "organization/*"
                :element [:ul
                          [nav-item "tasks" (tr [:view.task.list/title]) TaskIcon]
                          [nav-item "agents" (tr [:view.agent.list/title]) AgentIcon]
                          [nav-item "places" (tr [:view.place.list/title]) PlaceIcon]
                          [nav-item "vehicles" (tr [:view.vehicle.list/title]) VehicleIcon]
                          [nav-item "shipments" (tr [:view.shipment.list/title]) ShipmentIcon]
                          [nav-item "plans" (tr [:view.plan.list/title]) PlanIcon]]}
               {:path "agent/*"
                :element [:ul
                          [nav-item "tasks" (tr [:view.task.list/title]) TaskIcon]
                          [nav-item "places" (tr [:view.agent.list/title]) PlaceIcon]]}
               {:path "*" :element [:<>]}]))
