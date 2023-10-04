(ns ui.components.inputs.popover-button
  (:require [headlessui-reagent.core :as ui]
            [ui.lib.floating :refer (use-floating)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.menu :refer (menu-class)]))

(defn popover-button [{:keys [icon label placeholder value value-to-label required class]}
                      & children]
  (let [{:keys [x y reference floating strategy]} (use-floating)
        value-label (and value (value-to-label value))]
    [ui/popover {:as "div" :class (class-names class "relative")}
     [ui/popover-button
      {:ref reference
       :class (class-names "w-full text-left")}
      [input {:icon icon
              :label label
              :placeholder placeholder
              :value value-label
              :required required}]]
     [ui/popover-panel
      {:as "div"
       :ref floating
       :style {:position strategy
               :top (or y 0)
               :left (or x 0)}
       :class (class-names menu-class "z-10")}
      (into [:<>] children)]]))
