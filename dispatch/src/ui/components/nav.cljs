(ns ui.components.nav
  (:require
   [react-feather
    :rename {Map MapIcon
             Calendar CalendarIcon
             Users UsersIcon
             LogIn LogInIcon
             LogOut LogOutIcon
             Settings SettingsIcon}]
   [ui.lib.router :refer (routes nav-link)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding-x)]))

(defn nav-item [to icon]
  [:li {:class "px-2"}
   [nav-link {:to to
              :class (fn [{:keys [isActive]}]
                       (class-names
                        "block p-2"
                        (if isActive
                          "text-neutral-50 border-b border-neutral-300"
                          "text-neutral-300")))}
    [:> icon]]])

(defn title [text]
  [:h1 {:class
        (class-names
         "font-semibold text-white text-sm sm:text-base lg:text-xl")}
   text])

(defn nav [main]
  [:<>
   [:header {:class (class-names
                     "relative z-20"
                     "grid grid-cols-3 items-center"
                     "border-b border-neutral-600"
                     "w-full h-[60px]"
                     padding-x)}
    [routes
     ["/register" [title (tr [:view.register/title])]]
     ["/login" [title (tr [:view.login/title])]]
     ["/fleet" [title (tr [:view.route/title])]]
     ["*" [title "Not found"]]]

    [:nav [:ul {:class "flex justify-center"}
           [nav-item "/login" LogInIcon]
           [nav-item "/fleet" MapIcon]
           [nav-item "/schedule" CalendarIcon]
           [nav-item "/seats" UsersIcon]
           [nav-item "/logout" LogOutIcon]]]
    [:div {:class "flex justify-end"}
     [:> SettingsIcon {:class "text-neutral-50"}]]]
   [:main {:class "w-full h-[calc(100%_-_60px)]"}
    main]])
