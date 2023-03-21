(ns ui.components.layout.bare
  (:require [ui.components.layout.header :refer (header)]))

(defn bare-layout [{:keys [title filters actions on-refresh]} & children]
  [:main {:class "flex flex-col w-full h-full min-w-0 min-h-0"}
   [header {:title title
            :filters filters
            :actions actions
            :on-refresh on-refresh}]
   (into [:<>] children)])
