(ns ui.views.admin.fleet.core
  (:require
   [react]
   [ui.hooks.use-route :refer (use-route route-context-provider)]
   [ui.views.admin.fleet.panel.core :refer (panel)]
   [ui.views.admin.fleet.map :refer (gmap)]))

(defn route-view []
  (let [props (use-route)]
    [:> route-context-provider {:value props}
     [:div {:class "relative flex flex-col lg:flex-row w-full h-full overflow-y-auto"}
      [panel "order-2 lg:order-1"]
      [gmap "order-1 lg:order-2"]]]))
