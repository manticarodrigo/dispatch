(ns ui.components.map
  (:require [react-feather :refer (Crosshair)]
            [ui.hooks.use-map :refer (use-map)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (button)]))

(defn gmap [class]
  (let [{!el :ref center :center} (use-map)]
    [:aside {:class (class-names class "relative w-full h-full")}
     [:div {:ref #(reset! !el %) :class "w-full h-full"}]
     [button {:aria-label (tr [:map/center])
              :label [:> Crosshair]
              :class (class-names "absolute"
                                  "right-3 sm:right-4 md:right-6 lg:right-8"
                                  "top-3 sm:top-4 md:top-6")
              :on-click center}]]))
