(ns ui.components.layout.sidebar
  (:require [ui.subs :refer (listen)]
            [ui.utils.string :refer (class-names)]))

(defn sidebar [& children]
  (let [sidebar-open (listen [:layout/sidebar-open])]
    [:aside
     {:class
      (class-names
       "z-20 xl:z-auto"
       "fixed xl:static"
       "pt-14 xl:pt-0"
       "w-full h-full"
       "bg-neutral-900 xl:bg-transparent"
       "transition xl:translate-x-0"
       (if sidebar-open "translate-x-0" "translate-x-[100%]"))}
     (into [:<>] children)]))
