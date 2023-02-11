(ns ui.components.title
  (:require [react-feather :rename {Plus PlusIcon}]
            [ui.lib.router :refer (link)]))

(defn title [{:keys [title subtitle create-link]}]
  [:div {:class "pb-4 flex justify-between items-center"}
   [:div
    [:h1 {:class "text-lg font-medium"} title]
    (when subtitle
      [:h2 {:class "text-xs font-light"} subtitle])]
   (when create-link
     [link {:to create-link :class "underline text-sm"}
      [:> PlusIcon {:class "inline mr-1 w-3 h-3"}] "Create"])])
