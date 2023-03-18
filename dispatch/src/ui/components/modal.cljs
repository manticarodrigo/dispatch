(ns ui.components.modal
  (:require [react :refer (Fragment)]
            [react-feather :rename {X CloseIcon}]
            ["@headlessui/react" :refer (Transition Dialog)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]))

(defn modal [{show :show
              title :title
              on-close :on-close}
             & children]
  [:> Transition
   {:appear true
    :show show
    :as Fragment}
   [:> Dialog
    {:as "div"
     :class "relative z-10"
     :on-close on-close}
    [:> (. Transition -Child)
     {:as Fragment
      :enter "ease-out duration-300"
      :enter-from "opacity-0"
      :enter-to "opacity-100"
      :leave "ease-in duration-200"
      :leave-from "opacity-100"
      :leave-to "opacity-0"}
     [:div {:class "fixed inset-0 bg-black/75 backdrop-blur"}]]
    [:div {:class "fixed inset-0 overflow-y-auto"}
     [:div {:class "flex min-h-full items-center justify-center p-4 text-center"}
      [:> (. Transition -Child)
       {:as Fragment
        :enter "ease-out duration-300"
        :enter-from "opacity-0 scale-95"
        :enter-to "opacity-100 scale-100"
        :leave "ease-in duration-200"
        :leave-from "opacity-100 scale-100"
        :leave-to "opacity-0 scale-95"}
       [:> (. Dialog -Panel)
        {:class "transform overflow-hidden rounded-2xl bg-neutral-900 text-left align-middle shadow-xl transition-all"}
        [:header {:class "flex justify-between items-center p-4 border-b border-neutral-700"}
         [:> (. Dialog -Title) {:as "h3" :class "text-sm font-medium leading-6"} title]
         [:button {:class (class-names
                           "transition"
                           "ml-2 p-2 rounded-full"
                           "text-neutral-400 hover:text-neutral-50 focus:text-neutral-50"
                           "hover:bg-neutral-700 focus:bg-neutral-700")
                   :on-click on-close}
          [:> CloseIcon {:class "w-4 h-4"}]
          [:span {:class "sr-only"} (tr [:verb/close])]]]
        (into [:<>] children)]]]]]])
