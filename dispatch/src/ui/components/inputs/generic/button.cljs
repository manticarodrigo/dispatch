(ns ui.components.inputs.generic.button
  (:require [ui.utils.string :refer (class-names)]))

(def box-class (class-names
                "rounded border border-neutral-800"
                "text-base text-neutral-100"
                "bg-neutral-900 shadow"))

(def box-padding-class "p-2.5 lg:py-2.5 lg:px-4")

(def box-interact-class (class-names
                         "hover:border-neutral-500 focus:border-neutral-500"
                         "hover:text-neutral-50 focus:text-neutral-50 active:text-neutral-50"
                         "hover:bg-neutral-700 focus:bg-neutral-800 active:bg-neutral-800"))

(def box-peer-interact-class (class-names
                              "peer-hover:border-neutral-500 peer-focus:border-neutral-500"
                              "peer-hover:text-neutral-50 peer-focus:text-neutral-50 peer-active:text-neutral-50"
                              "peer-hover:bg-neutral-700 peer-focus:bg-neutral-800 peer-active:bg-neutral-800"))

(def base-button-class (class-names
                        "outline-0 focus:outline-none"
                        box-class
                        box-padding-class))

(def button-class (class-names
                   base-button-class
                   box-interact-class))

(defn button [{type :type
               aria-label :aria-label
               label :label
               class :class
               on-click :on-click}]
  [:button {:type type
            :aria-label aria-label
            :class (class-names
                    class
                    button-class)
            :on-click on-click}
   label])
