(ns ui.components.inputs.generic.modal
  (:require [react :refer (Fragment)]
            ["@headlessui/react" :refer (Transition Dialog)]))

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
     [:div {:class "fixed inset-0 bg-black bg-opacity-50"}]]
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
        {:class "w-full max-w-md transform overflow-hidden rounded-2xl bg-neutral-900 p-6 text-left align-middle shadow-xl transition-all"}
        [:> (. Dialog -Title) {:as "h3" :class "mb-4 text-lg font-medium leading-6"} title]
        children]]]]]])
