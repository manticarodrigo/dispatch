(ns ui.components.header.menu
  (:require [react-feather :rename {Settings SettingsIcon}]
            [ui.lib.router :refer (use-routes)]
            [ui.components.inputs.menu :rename {menu menu-input}]
            [ui.components.inputs.language-radio-group :refer (language-radio-group)]))

(defn menu []
  (use-routes [{:path "admin/*"
                :element [menu-input {:label [:> SettingsIcon]
                                      :items [[{:label  "Register" :to "/register"}
                                               {:label "Login" :to "/login"}]
                                              {:label "Sign out..." :to "/logout"}]
                                      :class-map {:button! "h-full"
                                                  :item "min-w-[12rem]"}}
                          [language-radio-group]]}
               {:path "seat/*"
                :element [menu-input {:label [:> SettingsIcon]
                                      :class-map {:button! "h-full"
                                                  :item "min-w-[12rem]"}}
                          [language-radio-group]]}
               {:path "*" :element [:<>]}]))
