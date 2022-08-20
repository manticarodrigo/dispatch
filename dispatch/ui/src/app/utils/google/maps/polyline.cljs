(ns app.utils.google.maps.polyline)

(set! *warn-on-infer* false)

(defonce ^:private !polyline (atom nil))

(defn set-polyline [gmap path]
  (let [polyline (js/google.maps.Polyline.
                  (clj->js {:path path
                            :geodisic true
                            :strokeColor "#3b82f6"
                            :strokeOpacity 0.75
                            :strokeWeight 6}))]
    (when @!polyline (.setMap @!polyline nil))
    (.setMap polyline gmap)
    (reset! !polyline polyline)
    polyline))
