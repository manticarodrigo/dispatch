(ns ui.components.map
  (:require ["react" :refer (useState useEffect)]
            ["react-dom" :refer (createPortal)]
            ["react-feather" :refer (Crosshair)]
            [reagent.core :as r]
            [ui.hooks.use-map :refer (use-map use-map-render)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (button)]))

(defn base-map []
  (let [!el (use-map)]
    (createPortal
     (r/as-element
      [:div {:ref #(reset! !el %) :class "w-full h-full"}])
     (js/document.getElementById "map-container"))))

(defn global-map []
  (let [[mounted set-mounted] (useState false)]

    (useEffect
     (fn []
       (set-mounted true)
       (fn []
         (set-mounted false)))
     #js[])

    (when mounted
      [base-map])))

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
