(ns ui.views.admin.fleet.map
  (:require [react-feather :refer (Crosshair)]
            [ui.subs :refer (listen)]
            [ui.hooks.use-route :refer (use-route-context)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.generic.button :refer (button)]))

(defn gmap [class]
  (let [bounds (listen [:route/bounds])
        {!el :ref center-route :center} (use-route-context)]
    [:div
     {:ref #(reset! !el %)
      :class (class-names class "w-full h-full")}
     (when bounds
       [button {:aria-label (tr [:location/center])
                :label [:> Crosshair {:class "w-5 h-5"}]
                :class "absolute bottom-4 right-2"
                :on-click center-route}])]))
