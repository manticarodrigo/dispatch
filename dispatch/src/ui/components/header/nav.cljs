(ns ui.components.header.nav
  (:require [react-feather :rename {Calendar CalendarIcon
                                    Users UsersIcon
                                    MapPin PinIcon}]
            [ui.lib.router :refer (nav-link use-routes)]
            [ui.utils.string :refer (class-names)]))

(defn nav-item [to icon]
  [:li {:class "px-1"}
   [nav-link {:to to
              :class (fn [{:keys [isActive]}]
                       (class-names
                        "block p-2"
                        (if isActive
                          "border-b border-neutral-300"
                          "text-neutral-600 hover:text-neutral-300 focus:text-neutral-300")))}
    [:> icon]]])

(defn nav []
  (use-routes [{:path "admin/*"
                :element [:nav [:ul {:class "flex justify-center"}
                                [nav-item "tasks" CalendarIcon]
                                [nav-item "seats" UsersIcon]
                                [nav-item "places" PinIcon]]]}
               {:path "seat/:id/*"
                :element [:nav [:ul {:class "flex justify-center"}
                                [nav-item "tasks" CalendarIcon]
                                [nav-item "places" PinIcon]]]}
               {:path "*" :element [:<>]}]))
