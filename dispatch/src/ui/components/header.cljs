(ns ui.components.header
  (:require
   [react-feather
    :rename {ArrowLeft ArrowLeftIcon
             Settings SettingsIcon}]
   [ui.lib.router :refer (routes link)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding-x)]
   [ui.components.icons :as icons]
   [ui.components.inputs.generic.menu :refer (menu)]
   [ui.components.nav :refer (nav)]))

(defn menu-item [to label]
  [link {:to to :class "block w-full h-full text-sm"} label])

(defn title [text]
  [:h1 {:class
        (class-names
         "sr-only"
         "font-semibold text-white text-sm sm:text-base lg:text-xl")}
   text])

(defn header []
  [:header {:class (class-names
                    "flex-shrink-0"
                    "flex justify-between items-center"
                    "border-b border-neutral-700"
                    padding-x
                    "w-full h-[60px]")}
   [routes
    ["/register" [icons/dispatch]]
    ["/login" [icons/dispatch]]
    ["/seats" [icons/dispatch]]
    ["/address" [icons/dispatch]]
    ["/route" [icons/dispatch]]
    ["*" [link {:to "/seats"} [:> ArrowLeftIcon]]]]

   [routes
    ["/register" [title (tr [:view.register/title])]]
    ["/login" [title (tr [:view.login/title])]]
    ["/seats" [title (tr [:view.fleet/title])]]
    ["/address" [title (tr [:view.address/title])]]
    ["/route" [title (tr [:view.route/title])]]
    ["*" [title "Not found"]]]

   [nav]

   [menu {:label [:> SettingsIcon]
          :items [[{:label [menu-item "/register" "Register"]}
                   {:label [menu-item "/login" "Login"]}]
                  {:label [menu-item "/logout" "Sign out..."]}]
          :class-map {:button! "h-full"
                      :item "min-w-[12rem]"}}]])
