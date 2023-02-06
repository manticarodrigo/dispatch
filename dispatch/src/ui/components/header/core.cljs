(ns ui.components.header.core
  (:require [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding-x)]
            [ui.components.header.button :refer (button)]
            [ui.components.header.nav :refer (nav)]
            [ui.components.header.menu :refer (menu)]))

(defn header []
  [:header
   {:class
    (class-names
     "flex-shrink-0"
     "flex justify-between items-center"
     "border-b border-neutral-800"
     padding-x
     "w-full h-[60px]")}
   [button]
   [nav]
   [menu]])
