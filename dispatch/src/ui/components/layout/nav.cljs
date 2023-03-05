(ns ui.components.layout.nav
  (:require [ui.subs :refer (listen)]
            [ui.utils.string :refer (class-names)]
            [ui.lib.router :refer (nav-link)]
            [react-feather :rename {Settings SettingsIcon}]
            [ui.components.icons.dispatch :rename {dispatch dispatch-icon}]
            [ui.components.inputs.menu :rename {menu menu-input}]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.language-radio-group :refer (language-radio-group)]))

(defn nav-item [to label icon]
  [:li
   [nav-link
    {:to to
     :class
     (fn [{:keys [isActive]}]
       (class-names
        "flex items-center"
        "mb-1"
        "rounded"
        "py-1 px-2"
        "text-sm"
        "hover:text-neutral-50 focus:text-neutral-50"
        "hover:bg-neutral-800 focus:bg-neutral-800"
        (if isActive
          "text-neutral-300 bg-neutral-800"
          "text-neutral-400")))}
    [:> icon {:class "w-4 h-4"}]
    [:span {:class "ml-2"} label]]])

(defn nav-header [{:keys [menu-items]}]
  [:div {:class (class-names
                 "py-4 px-4"
                 "flex justify-between items-center"
                 "w-full")}
   [dispatch-icon {:class "w-4 h-4"}]
   [menu-input
    {:label [:> SettingsIcon {:class "w-4 h-4"}]
     :items menu-items
     :class-map {:button! "h-full"
                 :item "min-w-[12rem]"}}]])

(defn nav [{:keys [nav-items menu-items]} & children]
  (let [nav-open (listen [:layout/nav-open])]
    [:div {:class "flex w-full h-full"}
     [:nav
      {:class
       (class-names
        "z-10"
        "fixed lg:static"
        "flex-shrink-0"
        "border-r border-neutral-700"
        "pt-10 lg:pt-0"
        "w-[225px] h-full"
        "bg-neutral-900 lg:bg-transparent"
        "shadow-lg lg:shadow-none"
        "transition lg:translate-x-0"
        (if nav-open "translate-x-0" "translate-x-[-100%]"))}
      [:div {:class "flex flex-col justify-between h-full"}
       [:div
        [nav-header {:menu-items menu-items}]
        [:div {:class "py-2 px-4"}
         [:ul (doall
               (for [[path label icon] nav-items]
                 ^{:key path}
                 [nav-item path (tr [label]) icon]))]]]
       [:div {:class "p-4"}
        [language-radio-group]]]]
     (into [:<>] children)]))
