(ns ui.components.inputs.input
  (:require [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (button-class)]))

(def label-class (class-names "block mb-2 text-sm text-neutral-50"))

(def input-class (class-names button-class "appearance-none w-full text-left"))

(defn input [{id :id
              type :type
              aria-label :aria-label
              label :label
              placeholder :placeholder
              value :value
              required :required
              class :class
              on-validate :on-validate
              on-change :on-change
              on-text :on-text}]
  [:div {:class class}
   [:label {:for id :class (if aria-label "sr-only" label-class)}
    (or aria-label label)]
   [:input {:id id
            :type (or type "text")
            :placeholder placeholder
            :value value
            :required required
            :class input-class
            :on-change (fn [e]
                         (let [v (-> e .-target .-value)
                               valid? (if on-validate (on-validate v) true)]
                           (when valid?
                             (when on-change (on-change e))
                             (when on-text (on-text v)))))}]])
