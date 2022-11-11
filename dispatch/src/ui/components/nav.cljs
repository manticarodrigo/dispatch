(ns ui.components.nav
  (:require
   [react-feather
    :rename {Map MapIcon
             Calendar CalendarIcon
             MapPin PinIcon
             BookOpen BookOpenIcon}]
   [ui.lib.router :refer (nav-link)]
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
  [:nav [:ul {:class "flex justify-center"}
         [nav-item "/fleet" MapIcon]
         [nav-item "/schedule" CalendarIcon]
         [nav-item "/waypoint" PinIcon]
         [nav-item "/history" BookOpenIcon]]])
