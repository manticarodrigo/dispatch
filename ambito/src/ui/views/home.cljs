(ns ui.views.home
  (:require [ui.components.icons.ambito :refer [ambito]]))

(defn view []
  [:div {:class "flex justify-center items-center w-full h-full"}
   [:div {:class "flex items-center"}
    [ambito]
    [:h1 {:class "ml-2 text-2xl"} "Ambito"]]])
