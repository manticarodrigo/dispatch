(ns ui.components.link-card
  (:require [react-feather :rename {ChevronRight ChevronRightIcon}]
            [ui.lib.router :refer (link)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.generic.button :refer (button-class)]))

(defn link-card [{:keys [to icon title subtitle detail]}]
  [link {:to to
         :class (class-names "block" button-class)}
   [:div {:class "flex justify-between w-full text-left"}
    [:div {:class "flex items-center"}
     [:div {:class "mr-2"} [:> icon {:class "w-4 h-4"}]]]
    [:div {:class "w-full"}
     [:div {:class "font-medium text-sm"} title]
     [:div {:class "font-light text-xs text-neutral-400"} subtitle]]
    [:div {:class "flex-shrink-0 flex items-center"}
     [:div {:class "flex flex-col items-end"} detail]
     [:div {:class "ml-2"} [:> ChevronRightIcon {:class "w-4 h-4"}]]]]])
