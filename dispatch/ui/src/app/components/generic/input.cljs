(ns app.components.generic.input
  (:require [app.utils.string :refer (class-names)]
            [app.components.generic.button :refer (button-class)]))

(def label-class (class-names "block mb-2 text-sm text-neutral-50"))

(def input-class (class-names button-class "w-full"))

(defn input [{id :id
              label :label
              class :class
              on-change :on-change
              on-text :on-text}]
  [:div {:class class}
   [:label {:for id :class label-class}
    label]
   [:input {:id id
            :type "text"
            :class input-class
            :on-change (fn [e]
                         (when on-change (on-change e))
                         (when on-text (on-text (-> e .-target .-value))))}]])
