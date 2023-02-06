(ns ui.components.inputs.accordion
  (:require [reagent.core :as r]
            ["@headlessui/react" :refer (Disclosure)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (button-class base-button-class box-peer-interact-class)]))

(def ^:private Button (.-Button Disclosure))
(def ^:private Panel (.-Panel Disclosure))

(defn accordion [{items :items
                  item-to-term :item-to-term
                  item-to-description :item-to-description
                  item-class :item-class}]
  [:<>
   (doall
    (for [[idx item] (map-indexed vector items)]
      [:> Disclosure {:key (or (:key item) idx)
                      :as "div"
                      :class (class-names item-class "w-full")}
       (fn [props]
         (r/as-element
          [:<>
           [:> Button {:class (class-names
                               "peer w-full"
                               button-class
                               (when (. props -open)
                                 "border-b-0 rounded-b-none shadow-none"))}
            (item-to-term item)]
           [:> Panel {:class (class-names
                              base-button-class
                              box-peer-interact-class
                              (when (. props -open)
                                "border-t-0 rounded-t-none"))} (item-to-description item)]]))]))])
