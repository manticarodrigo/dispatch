(ns ui.components.header.core
  (:require [ui.utils.string :refer (class-names)]
            [ui.components.header.button :refer (button)]
            [ui.components.header.nav :refer (nav)]
            [ui.components.header.menu :refer (menu)]))

(defn header []
  [:nav
   {:class
    (class-names
     "flex-shrink-0"
     "border-b lg:border-b-0 lg:border-r border-neutral-800"
     "w-full h-[60px] lg:h-full lg:w-[225px]")}
   [:div {:class (class-names
                  "py-4 px-4"
                  "flex justify-between items-center"
                  "w-full")}
    [button] [menu]]
   [:div {:class "py-2 px-4"} [nav]]])
