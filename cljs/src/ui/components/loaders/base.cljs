(ns ui.components.loaders.base
  (:require [ui.components.icons.dispatch :rename {dispatch dispatch-icon}]))

(defn loader []
  [:div {:class "flex justify-center items-center h-full w-full"}
   [:div {:class "animate-pulse"}
    [dispatch-icon {:width 36 :height 36}]]])
