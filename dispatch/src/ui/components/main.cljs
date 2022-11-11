(ns ui.components.main
  (:require
   [react]
   [ui.hooks.use-route :refer (use-route route-context-provider)]
   [ui.components.header :refer (header)]
   [ui.components.panel :refer (panel)]
   [ui.components.map :refer (gmap)]))

(defn main [& children]
  (let [props (use-route)]
    [:> route-context-provider {:value props}
     [:div {:class "flex flex-col w-full h-full"}
      [:main {:class "relative flex flex-col lg:flex-row w-full h-full overflow-hidden"}
       [panel "flex-shrink-0 order-2 lg:order-1"
        [:<>
         [header]
         (into [:<>] children)]]
       [gmap "flex-shrink-0 lg:shrink order-1 lg:order-2"]]]]))
