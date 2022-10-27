(ns app.views.route.core
  (:require
   [react]
   [app.hooks.use-route :refer (use-route route-context-provider)]
   [app.views.route.nav.core :refer (nav)]
   [app.views.route.map :refer (gmap)]))

(defn route-view []
  (let [props (use-route)]
    [:> route-context-provider {:value props}
     [:div {:class "relative flex flex-col lg:flex-row w-full lg:h-screen overflow-y-auto lg:overflow-hidden"}
      [nav "order-2 lg:order-1"]
      [gmap "order-1 lg:order-2"]]]))
