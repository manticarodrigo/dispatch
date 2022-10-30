(ns ui.components.inputs.generic.accordion
  (:require [reagent.core :as r]
            ["@headlessui/react" :refer (Disclosure)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.generic.button :refer (button-class base-button-class)]))

(def ^:private Button (.-Button Disclosure))
(def ^:private Panel (.-Panel Disclosure))

(defn accordion [{items :items
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
                                 "border-b-0 rounded-b-none"))}
            [:div {:class "flex justify-between"}
             (:name item)
             [:span {:class "flex items-center flex-shrink-0"}]]]
           [:> Panel {:class (class-names
                              base-button-class
                              "peer-hover:border-neutral-50 peer-focus:border-neutral-50"
                              "peer-hover:text-neutral-50 peer-focus:text-neutral-50 peer-active:text-neutral-50"
                              "peer-hover:bg-neutral-700 peer-focus:bg-neutral-800 peer-active:bg-neutral-800"
                              (when (. props -open)
                                "border-t-0 rounded-t-none"))} (:description item)]]))]))])
