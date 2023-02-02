(ns ui.components.header
  (:require
   [react-feather
    :rename {ArrowLeft ArrowLeftIcon
             Settings SettingsIcon}]
   [ui.lib.router :refer (routes use-navigate)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding-x)]
   [ui.components.icons.dispatch :refer (dispatch)]
   [ui.components.inputs.menu :refer (menu)]
   [ui.components.nav :refer (nav)]
   [ui.components.inputs.language-radio-group :refer (language-radio-group)]))

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
    ["/" [dispatch]]
    ["/register" [dispatch]]
    ["/login" [dispatch]]
    ["/admin/routes" [dispatch]]
    ["/admin/routes/*" [back-button]]
    ["/admin/stops/*" [back-button]]
    ["/admin/seats" [dispatch]]
    ["/admin/seats/*" [back-button]]
    ["/admin/addresses" [dispatch]]
    ["/admin/addresses/*" [back-button]]
    ["*" [back-button]]]

   [nav]

   [menu {:label [:> SettingsIcon]
          :items [[{:label  "Register" :to "/register"}
                   {:label "Login" :to "/login"}]
                  {:label "Sign out..." :to "/logout"}]
          :class-map {:button! "h-full"
                      :item "min-w-[12rem]"}}
    [language-radio-group]]])
