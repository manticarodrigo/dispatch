(ns ui.components.status-detail
  (:require [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]))

(defn status-detail [{:keys [active? text]}]
  [:div {:class "flex flex-col items-end"}
   [:div {:class "flex items-center text-xs text-neutral-400 capitalize"}
    [:div {:class (class-names
                   "mr-1 rounded-full w-2 h-2"
                   (if active? "bg-green-500" "bg-amber-500"))}]
    (tr [:noun/status])]
   [:div {:class "flex items-center text-xs text-left text-neutral-200"}
    text]])
