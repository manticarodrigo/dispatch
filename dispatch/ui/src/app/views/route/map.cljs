(ns app.views.route.map
  (:require [react-feather :refer (Crosshair)]
            [app.subs :refer (listen)]
            [app.hooks.use-route :refer (use-route-context)]
            [app.utils.i18n :refer (tr)]
            [app.utils.string :refer (class-names)]
            [app.components.generic.button :refer (button)]))

(defn gmap [class]
  (let [bounds (listen [:route/bounds])
        {!el :ref center-route :center} (use-route-context)]
    [:div
     {:ref #(reset! !el %)
      :class (class-names class "w-screen h-[calc(100vh_-_60px)] lg:h-full")}
     (when bounds
       [button {:aria-label (tr [:location/center])
                :label [:> Crosshair {:class "w-5 h-5"}]
                :class "absolute bottom-4 right-2"
                :on-click center-route}])]))
