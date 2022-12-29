(ns ui.lib.google.maps.polyline)

(defonce ^:private !polyline (atom nil))

(defn clear-polyline! []
  (when @!polyline
    (.setMap ^js @!polyline nil)
    (reset! !polyline nil)))

(defn set-polyline [gmap path]
  (let [polyline (js/google.maps.Polyline.
                  (clj->js {:path path
                            :geodisic true
                            :strokeColor "#3b82f6"
                            :strokeOpacity 0.75
                            :strokeWeight 6}))]
    (clear-polyline!)
    (.setMap polyline gmap)
    (reset! !polyline polyline)
    polyline))
