(ns ui.lib.google.maps.marker
  (:require [cljs-bean.core :refer (->js)]))

(defonce ^:private !markers (atom []))

(defn create-marker! [options]
  (let [marker (js/google.maps.Marker. (->js options))]
    (swap! !markers conj marker)
    marker))

(defn clear-markers! []
  (doseq [^js marker @!markers] (.setMap marker nil))
  (reset! !markers []))

(defn fit-bounds-to-visible-markers [^js gmap]
  (let [bounds (js/google.maps.LatLngBounds.)]
    (if (seq @!markers)
      (do
        (doseq [^js marker @!markers]
          (.extend bounds (.getPosition marker)))
        (.fitBounds gmap bounds)
        (.panToBounds gmap bounds))
      (do (.setZoom gmap 2)
          (.setCenter gmap (->js {:lat 0 :lng 0}))))))
