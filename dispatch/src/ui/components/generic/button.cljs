(ns ui.components.generic.button
  (:require [ui.utils.string :refer (class-names)]))

(def base-button-class (class-names
                        "p-2.5"
                        "outline-0 rounded border border-neutral-500"
                        "text-base text-neutral-100"
                        "bg-neutral-900"))

(def hover-button-class (class-names
                         "hover:border-neutral-50 focus:border-neutral-50"
                         "hover:text-neutral-50 focus:text-neutral-50 active:text-neutral-50"
                         "hover:bg-neutral-700 focus:bg-neutral-800 active:bg-neutral-800"))

(def button-class (class-names
                   base-button-class
                   hover-button-class))

(defn button [{aria-label :aria-label
               label :label
               class :class
               on-click :on-click}]
  [:button {:aria-label aria-label
            :class (class-names
                    class
                    button-class)
            :on-click on-click}
   label])
