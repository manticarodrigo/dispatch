(ns ui.views.admin.fleet.core
  (:require
   [react]
   [ui.hooks.use-route :refer (use-route route-context-provider)]
   [ui.views.admin.fleet.panel.core :refer (panel)]
   [ui.views.admin.fleet.map :refer (gmap)]))

(defn route-view []
  (let [props (use-route)]
    [:> route-context-provider {:value props}
     [:div {:class "relative flex flex-col lg:flex-row w-full h-full"}
      [panel "flex-shrink-0 order-2 lg:order-1"]
      [gmap "flex-shrink-0 lg:shrink order-1 lg:order-2"]]]))
