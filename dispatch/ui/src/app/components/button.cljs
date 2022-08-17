(ns app.components.button
  (:require [app.utils.string :refer (class-names)]))

(defn button [{label :label
               class :class
               on-click :on-click}]
  [:button {:class (class-names
                    class
                    "p-2.5"
                    "rounded border border-white/[0.4] focus:outline-0 focus:border-white/[0.8]"
                    "text-white/[0.8] hover:text-white font-medium"
                    "bg-white/[0.2] hover:bg-white/[0.4]")
            :on-click on-click}
   label])
