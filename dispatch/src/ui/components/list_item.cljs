(ns ui.components.list-item
  (:require [react-feather :rename {ChevronRight ChevronRightIcon}]))

(defn list-item [{:keys [icon title subtitle detail]}]
  [:div {:class "flex justify-between w-full text-left"}
   [:div {:class "flex items-center"}
    [:div {:class "mr-2"} [:> icon {:class "w-4 h-4"}]]]
   [:div {:class "w-full"}
    [:div {:class "font-medium text-sm"} title]
    [:div {:class "font-light text-xs text-neutral-400"} subtitle]]
   [:div {:class "flex-shrink-0 flex items-center"}
    [:div {:class "flex flex-col items-end"} detail]
    [:div {:class "ml-2"} [:> ChevronRightIcon {:class "w-4 h-4"}]]]])
