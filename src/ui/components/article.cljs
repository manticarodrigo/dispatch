(ns ui.components.article
  (:require [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (box-class box-padding-class)]))

(defn article [{:keys [icon title subtitle detail]}]
  [:article {:class (class-names "block" box-class box-padding-class)}
   [:div {:class "flex justify-between w-full text-left"}
    [:div {:class "flex items-center"}
     [:div {:class "mr-2"} [:> icon {:class "w-4 h-4"}]]]
    [:div {:class "w-full"}
     [:h1 {:class "font-medium text-sm"} title]
     [:h2 {:class "font-light text-xs text-neutral-400"} subtitle]]
    [:aside {:class "flex-shrink-0"}
     detail]]])
