(ns ui.views.admin.fleet.panel.core
  (:require
   [ui.utils.string :refer (class-names)]
   [ui.views.admin.fleet.panel.overview :refer (overview)]))

(defn panel [class]
  [:section {:class
             (class-names
              class
              "z-10 relative flex-none"
              "w-full lg:w-[450px] h-full")}
   [overview]])
