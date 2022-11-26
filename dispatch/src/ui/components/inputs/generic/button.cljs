(ns ui.components.inputs.generic.button
  (:require [ui.utils.string :refer (class-names)]))

(def box-class (class-names
                "rounded border border-neutral-800"
                "text-base text-neutral-100"
                "bg-neutral-900 shadow"))

(def box-padding-class "p-2.5 lg:py-2.5 lg:px-4")

(def box-interact-class (class-names
                         "hover:border-neutral-500 focus:border-neutral-600"
                         "hover:bg-neutral-800 focus:bg-neutral-800 active:bg-neutral-800"))

(def box-peer-interact-class (class-names
                              "peer-hover:border-neutral-500 peer-focus:border-neutral-600"
                              "peer-hover:bg-neutral-800 peer-focus:bg-neutral-800 peer-active:bg-neutral-800"))

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
