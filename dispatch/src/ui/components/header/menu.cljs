(ns ui.components.header.menu
  (:require [react-feather :rename {Settings SettingsIcon}]
            [ui.lib.router :refer (use-routes)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.menu :rename {menu menu-input}]
            [ui.components.inputs.language-radio-group :refer (language-radio-group)]))

(defn settings-icon []
  [:> SettingsIcon {:class "w-4 h-4"}])

(defn menu []
  (use-routes [{:path "organization/*"
                :element [menu-input {:label [settings-icon]
                                      :items [[{:label  (tr [:view.subscription/title]) :to "subscription/payment"}]
                                              {:label (str (tr [:misc/sign-out]) "...") :to "/logout"}]
                                      :class-map {:button! "h-full"
                                                  :item "min-w-[12rem]"}}
                          [language-radio-group]]}
               {:path "agent/*"
                :element [menu-input {:label [settings-icon]
                                      :class-map {:button! "h-full"
                                                  :item "min-w-[12rem]"}}
                          [language-radio-group]]}
               {:path "*" :element [:<>]}]))
