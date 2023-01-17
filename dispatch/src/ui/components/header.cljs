(ns ui.components.header
  (:require
   [react-feather
    :rename {ArrowLeft ArrowLeftIcon
             Settings SettingsIcon}]
   [ui.lib.router :refer (routes use-navigate)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding-x)]
   [ui.components.icons :as icons]
   [ui.components.inputs.generic.menu :refer (menu)]
   [ui.components.nav :refer (nav)]))

(defn title [text]
  [:h1 {:class
        (class-names
         "sr-only"
         "font-semibold text-white text-sm sm:text-base lg:text-xl")}
   text])

(defn back-button []
  (let [navigate (use-navigate)]
    [:button {:on-click #(navigate -1)} [:> ArrowLeftIcon]]))

(defn header []
  [:header {:class (class-names
                    "flex-shrink-0"
                    "flex justify-between items-center"
                    "border-b border-neutral-800"
                    padding-x
                    "w-full h-[60px]")}
   [routes
    ["/" [icons/dispatch]]
    ["/register" [icons/dispatch]]
    ["/login" [icons/dispatch]]
    ["/routes" [icons/dispatch]]
    ["/routes/*" [back-button]]
    ["/stops/*" [back-button]]
    ["/seats" [icons/dispatch]]
    ["/seats/*" [back-button]]
    ["/addresses" [icons/dispatch]]
    ["/addresses/*" [back-button]]
    ["*" [back-button]]]

   [routes
    ["/" [title "Home"]]
    ["/register" [title (tr [:view.register/title])]]
    ["/login" [title (tr [:view.login/title])]]
    ["/seats" [title (tr [:view.fleet/title])]]
    ["/addresses" [title (tr [:view.address/title])]]
    ["/routes" [title (tr [:view.route/title])]]
    ["*" [title "Not found"]]]

   [nav]

   [menu {:label [:> SettingsIcon]
          :items [[{:label  "Register" :to "/register"}
                   {:label "Login" :to "/login"}]
                  {:label "Sign out..." :to "/logout"}]
          :class-map {:button! "h-full"
                      :item "min-w-[12rem]"}}]])
