(ns ui.components.inputs.tooltip
  (:require [headlessui-reagent.core :as ui]
            [ui.lib.floating :refer (use-floating)]
            [ui.utils.string :refer (class-names)]))

(defn tooltip [anchor & children]
  (let [{:keys [x y reference floating strategy]} (use-floating {:strategy "fixed"})]
    [ui/popover {:as "span" :class "relative"}
     [ui/popover-button
      {:ref reference
       :class (class-names "text-left")}
      anchor]
     [ui/popover-panel
      {:as "div"
       :ref floating
       :style {:position strategy
               :top (or y 0)
               :left (or x 0)}
       :class (class-names "z-10 rounded p-4 bg-neutral-900/60 backdrop-blur-md shadow")}
      (into [:<>] children)]]))
