(ns ui.components.layout.nav
  (:require [ui.subs :refer (listen)]
            [ui.utils.string :refer (class-names)]
            [ui.lib.router :refer (nav-link)]))

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

(defn nav [& children]
  (let [nav-open (listen [:layout/nav-open])]
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
     (into [:<>] children)]))
