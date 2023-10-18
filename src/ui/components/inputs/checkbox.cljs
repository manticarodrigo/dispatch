(ns ui.components.inputs.checkbox
  (:require ["react-feather" :rename {Check CheckIcon
                                      X DisabledIcon}]
            [headlessui-reagent.core :as ui]
            [ui.components.inputs.button :refer (box-class box-interact-class)]
            [ui.utils.string :refer (class-names)]))

(defn checkbox [{:keys [checked class disabled on-change]}]
  [ui/switch {:class (class-names
                      class
                      box-class
                      box-interact-class
                      (if checked
                        "text-neutral-50"
                        "text-neutral-800 hover:text-neutral-700 focus-within:text-neutral-700 active:text-neutral-700"))
              :checked checked
              :disabled disabled
              :on-change #(on-change #js{:target #js{:checked %}})}
   (if disabled
     [:> DisabledIcon {:class "w-4 h-4"}]
     [:> CheckIcon {:class "w-4 h-4"}])])
