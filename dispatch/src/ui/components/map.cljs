(ns ui.components.map
  (:require [react-dom :refer (createPortal)]
            [reagent.core :as r]
            [react-feather :refer (Crosshair)]
            [ui.hooks.use-map :refer (use-map use-map-render)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (button)]))

(defn global-map []
  (let [!el (use-map)]
    (createPortal
     (r/as-element
      [:div {:ref #(reset! !el %) :class "w-full h-full"}])
     (js/document.getElementById "map-container"))))

(defn gmap [class]
  (let [{:keys [ref center]} (use-map-render)]
    [:aside {:class (class-names class "relative w-full h-full")}
     [:div {:ref ref :class "w-full h-full"}]
     [button {:aria-label (tr [:map/center])
              :label [:> Crosshair]
              :class (class-names "absolute"
                                  "right-3 sm:right-4 md:right-6 lg:right-8"
                                  "top-3 sm:top-4 md:top-6")
              :on-click center}]]))
