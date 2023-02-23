(ns ui.components.inputs.button
  (:require [ui.utils.string :refer (class-names)]))

(def box-class (class-names
                "rounded border border-neutral-700"
                "bg-neutral-800 shadow"))

(def box-padding-class "py-1 px-2")

(def box-interact-class (class-names
                         "transition"
                         "hover:border-neutral-500 focus:border-neutral-500"
                         "hover:bg-neutral-700 focus:bg-neutral-700 active:bg-neutral-700"))

(def box-peer-interact-class (class-names
                              "transition"
                              "peer-hover:border-neutral-500 peer-focus:border-neutral-500"
                              "peer-hover:bg-neutral-700 peer-focus:bg-neutral-700 peer-active:bg-neutral-700"))

(def base-button-class (class-names
                        box-class
                        box-padding-class
                        "outline-0 outline-none"
                        "text-base text-neutral-100"))

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
