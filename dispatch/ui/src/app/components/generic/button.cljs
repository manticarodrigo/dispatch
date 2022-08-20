(ns app.components.generic.button
  (:require [app.utils.string :refer (class-names)]))

(def button-class (class-names
                   "p-2.5"
                   "outline-0 rounded border border-neutral-500 hover:border-neutral-50 focus:border-neutral-50"
                   "text-base text-neutral-100 hover:text-neutral-50 focus:text-neutral-50 active:text-neutral-50"
                   "bg-neutral-900 hover:bg-neutral-700 focus:bg-neutral-800 active:bg-neutral-800"))

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
