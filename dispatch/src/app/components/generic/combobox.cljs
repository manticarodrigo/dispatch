(ns app.components.generic.combobox
  (:require [react :refer (Fragment)]
            ["@headlessui/react" :refer (Combobox Transition)]
            [app.utils.i18n :refer (tr)]
            [app.utils.string :refer (class-names)]
            [app.components.generic.input :refer (label-class input-class)]))

(def ^:private Label (.-Label Combobox))
(def ^:private Input (.-Input Combobox))
(def ^:private Option (.-Option Combobox))
(def ^:private Options (.-Options Combobox))

(defn combobox [{aria-label :aria-label
                 label :label
                 class :class
                 value :value
                 options :options
                 option-to-label :option-to-label
                 option-to-value :option-to-value
                 on-text :on-text
                 on-change :on-change}]
  [:> Combobox {:as "div"
                :class (class-names class "relative")
                :value value
                :on-change on-change}
   [:> Label {:class (if aria-label "sr-only" label-class)} (or aria-label label)]
   [:> Input {:class input-class :on-change #(on-text (-> % .-target .-value))}]
   [:> Transition
    {:as Fragment
     :leave "transition ease-in duration-100"
     :leave-from "opacity-100"
     :leave-to "opacity-0"}
    [:> Options {:class (class-names
                         "ring-1 ring-neutral-50 ring-opacity-5 focus:outline-none"
                         "absolute rounded mt-2 py-1 max-h-60 w-full"
                         "text-sm text-neutral-200"
                         "bg-neutral-800 shadow shadow-neutral-800 overflow-auto")}
     (when (= (count options) 0)
       [:div {:class "relative cursor-default select-none p-2"}
        (tr [:location/search-empty])])
     (doall
      (for [option options]
        (let [l (if option-to-label
                  (option-to-label option)
                  (:label option))
              v (if option-to-value
                  (option-to-value option)
                  (:key option))]
          [:> Option {:key v  :value v :class (fn [props]

                                                (let [{active :active checked :checked} (js->clj props :keywordize-keys true)]
                                                  (class-names
                                                   "relative cursor-pointer select-none p-2"
                                                   (when (or checked active)
                                                     (class-names
                                                      "text-neutral-50"
                                                      (if checked "bg-neutral-600" "bg-neutral-700"))))))} l])))]]])
