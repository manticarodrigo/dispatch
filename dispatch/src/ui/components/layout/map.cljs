(ns ui.components.layout.map
  (:require [ui.components.layout.sidebar :refer (sidebar)]
            [ui.components.map :refer (gmap)]))

(defn map-layout [& children]
  [:div {:class "flex w-full h-full"}
   [:main {:class "flex-shrink-0 flex flex-col w-full xl:w-[450px] h-full"}
    (into [:<>] children)]
   [sidebar
    [gmap]]])
