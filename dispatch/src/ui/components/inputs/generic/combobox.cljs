(ns ui.components.inputs.generic.combobox
  (:require [react :refer (Fragment useState)]
            [react-feather :rename {ChevronDown ChevronDownIcon}]
            ["@headlessui/react" :refer (Combobox Transition)]
            [clojure.string :as s]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.generic.input :refer (label-class input-class)]))

(def ^:private Label (.-Label Combobox))
(def ^:private Input (.-Input Combobox))
(def ^:private Button (.-Button Combobox))
(def ^:private Option (.-Option Combobox))
(def ^:private Options (.-Options Combobox))

(defn combobox [{aria-label :aria-label
                 label :label
                 placeholder :placeholder
                 class :class
                 value :value
                 options :options
                 option-to-label :option-to-label
                 option-to-value :option-to-value
                 on-text :on-text
                 on-change :on-change}]
  (let [[query set-query] (useState "")
        filtered-options (filter #(-> %
                                      option-to-label
                                      s/lower-case
                                      (s/starts-with? (s/lower-case query)))
                                 options)
        on-query (fn [e]
                   (if on-text
                     (on-text (-> e .-target .-value))
                     (set-query (-> e .-target .-value))))]
    [:> Combobox {:as "div"
                  :class (class-names class "relative")
                  :value value
                  :on-change on-change}
     [:> Label {:class (if aria-label "sr-only" label-class)} (or aria-label label)]
     [:> Button {:as "div" :class "relative"}
      [:> Input {:placeholder placeholder
                 :class input-class
                 :style {:padding-right "3rem"}
                 :on-change on-query}]
      [:div {:class "cursor-text absolute top-[50%] translate-y-[-50%] right-0 pr-2 lg:pr-4"}
       [:> ChevronDownIcon]]]
     [:> Transition
      {:as Fragment
       :leave "transition ease-in duration-100"
       :leave-from "opacity-100"
       :leave-to "opacity-0"}
      [:> Options {:static true
                   :class (class-names
                           "ring-1 ring-neutral-50 ring-opacity-5 focus:outline-none"
                           "absolute rounded mt-2 py-1 max-h-60 w-full"
                           "text-sm text-neutral-200"
                           "bg-neutral-800 shadow shadow-neutral-800 overflow-auto")}
       (when (= (count filtered-options) 0)
         [:div {:class "relative cursor-default select-none p-2"}
          (tr [:location/search-empty])])
       (doall
        (for [option filtered-options]
          (let [l (if option-to-label
                    (option-to-label option)
                    (:label option))
                v (if option-to-value
                    (option-to-value option)
                    (:key option))]
            [:> Option {:key v
                        :value v
                        :class (fn [props]
                                 (let [{active :active checked :checked} (js->clj props :keywordize-keys true)]
                                   (class-names
                                    "relative cursor-pointer select-none p-2"
                                    (when (or checked active)
                                      (class-names
                                       "text-neutral-50"
                                       (if checked "bg-neutral-600" "bg-neutral-700"))))))} l])))]]]))
