(ns ui.views.admin.fleet.panel.container
  (:require [ui.utils.string :refer (class-names)]))

(defn container [class & children]
  [:section {:class
             (class-names
              class
              "z-10 relative flex-none"
              "w-full lg:w-[450px] h-full"
              "text-neutral-50 bg-neutral-900")}
   (into [:<>] children)])
