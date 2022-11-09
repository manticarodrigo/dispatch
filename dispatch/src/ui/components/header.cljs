(ns ui.components.header
  (:require
   [react-feather
    :rename {Map MapIcon
             Calendar CalendarIcon
             MapPin PinIcon
             BookOpen BookOpenIcon
             Settings SettingsIcon}]
   [ui.lib.router :refer (routes nav-link link)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding-x)]
   [ui.components.inputs.generic.menu :refer (menu)]))

(defn nav-item [to icon]
  [:li {:class "px-2"}
   [nav-link {:to to
              :class (fn [{:keys [isActive]}]
                       (class-names
                        "block p-2"
                        (if isActive
                          "border-b border-neutral-300"
                          "text-neutral-600 hover:text-neutral-300 focus:text-neutral-300")))}
    [:> icon]]])

(defn menu-item [to label]
  [link {:to to :class "block w-full h-full text-sm"} label])

(defn title [text]
  [:h1 {:class
        (class-names
         "sr-only lg:not-sr-only"
         "font-semibold text-white text-sm sm:text-base lg:text-xl")}
   text])

(defn header []
  [:header {:class (class-names
                    "flex-shrink-0"
                    "grid grid-cols-3 items-center"
                    "border-b border-neutral-600"
                    padding-x
                    "w-full h-[60px]")}
   [:div {:class "flex"}
    [:svg {:xmlns "http://www.w3.org/2000/svg"
           :viewBox "0 0 1005.12 888.12"
           :fill "currentColor"
           :class "mr-2 w-7 h-7"}
     [:path {:d "M987 644c-24 41.77-67.21 66.64-115.37 66.64H410.32a44.4 44.4 0 1 1 0-88.8h461.32c23.16 0 34.67-15.63 38.5-22.16 3.84-6.68 11.51-24.44 0-44.47L589.62 0h102.44L987 510.91c24.16 41.62 24.16 91.35 0 133.09Z"}]
     [:path {:d "M740.08 527a44.46 44.46 0 0 1-60.67-16.34L448.68 111c-11.65-20-30.83-22.16-38.5-22.16s-26.86 2.13-38.51 22.3L51.29 666.05 0 577.4 294.81 66.63A132 132 0 0 1 410.18 0c48.16 0 91.21 24.86 115.36 66.63L756.42 466.3a44.46 44.46 0 0 1-16.34 60.7Z"}]
     [:path {:d "m897.5 799.32-51.29 88.8-589.9-.12c-48.17 0-91.22-24.86-115.37-66.64a131.81 131.81 0 0 1 0-133.12l230.88-399.68a44.36 44.36 0 0 1 76.86 44.32L218 732.55a43.26 43.26 0 0 0 0 44.45 45.25 45.25 0 0 0 11.93 13.64 43.06 43.06 0 0 0 26.43 8.52Z"}]]

    [routes
     ["/register" [title (tr [:view.register/title])]]
     ["/login" [title (tr [:view.login/title])]]
     ["/fleet" [title (tr [:view.fleet/title])]]
     ["/waypoint" [title (tr [:view.waypoint/title])]]
     ["/schedule" [title (tr [:view.schedule/title])]]
     ["*" [title "Not found"]]]]

   [:nav [:ul {:class "flex justify-center"}
          [nav-item "/fleet" MapIcon]
          [nav-item "/schedule" CalendarIcon]
          [nav-item "/waypoint" PinIcon]
          [nav-item "/history" BookOpenIcon]]]

   [:div {:class "flex justify-end"}
    [menu {:label [:> SettingsIcon]
           :items [[{:label [menu-item "/register" "Register"]}
                    {:label [menu-item "/login" "Login"]}]
                   {:label [menu-item "/logout" "Sign out..."]}]
           :class-map {:button! "h-full"
                       :item "min-w-[12rem]"}}]]])
