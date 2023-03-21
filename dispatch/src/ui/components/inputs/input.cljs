(ns ui.components.inputs.input
  (:require [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (button-class)]))

(def label-class (class-names "block mb-2 text-sm text-neutral-50"))

(def input-class (class-names
                  button-class
                  "appearance-none"
                  "w-full truncate"
                  "text-left text-sm font-light"
                  "placeholder:text-sm placeholder:text-neutral-400"))

(defn input [{type :type
              aria-label :aria-label
              label :label
              placeholder :placeholder
              icon :icon
              value :value
              required :required
              class :class
              on-validate :on-validate
              on-change :on-change
              on-text :on-text}]
  [:label {:class (class-names class "block")}
   [:span
    {:class (if aria-label
              "sr-only"
              (class-names
               label-class
               (when required
                 "after:content-['*'] after:ml-0.5 after:text-red-500")))}
    (or aria-label label)]
   [:div {:class "relative"}
    (when icon
      [:div {:class "absolute inset-y-0 left-0 flex items-center pl-2 pointer-events-none"}
       [:> icon {:class "w-4 h-4 text-neutral-50"}]])
    [:input {:type (or type "text")
             :placeholder placeholder
             :value value
             :required required
             :autoComplete "off"
             :autoCorrect "off"
             :autoCapitalize "off"
             :spellCheck "false"
             :class (class-names input-class (when icon "pl-8"))
             :on-change (fn [e]
                          (let [v (-> e .-target .-value)
                                valid? (if on-validate (on-validate v) true)]
                            (when valid?
                              (when on-change (on-change e))
                              (when on-text (on-text v)))))}]]])
