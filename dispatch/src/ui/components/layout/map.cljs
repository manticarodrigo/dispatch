(ns ui.components.layout.map
  (:require [ui.components.layout.sidebar :refer (sidebar)]
            [ui.components.map :refer (gmap)]
            [ui.components.layout.header :refer (header)]))

(defn map-layout [header-props & children]
  [:div {:class "flex w-full h-full"}
   [:main {:class "flex-shrink-0 flex flex-col w-full xl:w-[450px] h-full"}
    [header header-props]
    (into [:<>] children)]
   [sidebar
    [gmap]]])
