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
     [:div {:class "relative flex flex-col lg:flex-row w-full h-full"}
      [panel "flex-shrink-0 order-2 lg:order-1"
       [:<>
        [header]
        (into [:main {:class "h-[calc(100%_-_60px)] overflow-y-auto"}] children)]]
      [gmap "flex-shrink-0 lg:shrink order-1 lg:order-2"]]]))
