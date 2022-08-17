(ns app.components.input
  (:require [app.utils.string :refer (class-names)]))

(defn input [{id :id
              label :label
              class :class
              on-click :on-click}]
  [:div {:class class}
   [:label {:for id :class "block mb-2 text-sm font-medium text-white/[0.8]"}
    label]
   [:input {:id id
            :type "text"
            :class (class-names
                    "p-2.5 w-full"
                    "rounded border border-white/[0.4]"
                    "focus:outline-0 focus:border-white/[0.8]"
                    "text-base text-white/[0.8] hover:text-white"
                    "bg-white/[0.2] hover:bg-white/[0.4]")
            :on-change on-click}]])
