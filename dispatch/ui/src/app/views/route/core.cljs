(ns app.views.route.core
  (:require
   [react]
   [app.hooks.use-route :refer (use-route route-context-provider)]
   [app.views.route.nav.core :refer (nav)]
   [app.views.route.map :refer (gmap)]))

(defn route-view []
  (let [props (use-route)]
    [:> route-context-provider {:value props}
     [:div {:class "relative flex w-full h-screen"}
      [:f> nav "order-1"]
      [:f> gmap "order-2"]]]))
