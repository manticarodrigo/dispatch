(ns ui.components.popover
  (:require [headlessui-reagent.core :as ui]
            [ui.lib.floating :refer (use-floating)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.menu :refer (menu-class)]))

(defn popover [anchor & children]
  (let [{:keys [x y reference floating strategy]} (use-floating)]
    [ui/popover {:as "div" :class "relative"}
     [ui/popover-button {:ref reference :as "div"}
      anchor]
     [ui/popover-panel
      {:as "div"
       :ref floating
       :style {:position strategy
               :top (or y 0)
               :left (or x 0)}
       :class (class-names menu-class "z-10")}
      (into [:<>] children)]]))
