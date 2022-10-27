(ns ui.utils.google.maps.polyline)

(defonce ^:private !polyline (atom nil))

(defn set-polyline [gmap path]
  (let [polyline (js/google.maps.Polyline.
                  (clj->js {:path path
                            :geodisic true
                            :strokeColor "#3b82f6"
                            :strokeOpacity 0.75
                            :strokeWeight 6}))]
    (when @!polyline (.setMap ^js @!polyline nil))
    (.setMap polyline gmap)
    (reset! !polyline polyline)
    polyline))
