(ns ui.components.generic.input
  (:require [ui.utils.string :refer (class-names)]
            [ui.components.generic.button :refer (button-class)]))

(def label-class (class-names "block mb-2 text-sm text-neutral-50"))

(def input-class (class-names button-class "w-full"))

(defn input [{id :id
              type :type
              label :label
              value :value
              required :required
              class :class
              on-change :on-change
              on-text :on-text}]
  [:div {:class class}
   [:label {:for id :class label-class}
    label]
   [:input {:id id
            :type (or type "text")
            :value value
            :required required
            :class input-class
            :on-change (fn [e]
                         (when on-change (on-change e))
                         (when on-text (on-text (-> e .-target .-value))))}]])