(ns ui.lib.google.maps.polyline
  (:require [cljs-bean.core :refer (->js)]
            [ui.utils.color :refer (get-color)]))

(defonce ^:private !polyline (atom nil))

(defn create-polyline [{:keys [path color opacity]}]
  (js/google.maps.Polyline.
   (->js {:path path
          :geodisic true
          :strokeColor color
          :strokeOpacity opacity
          :strokeWeight 6})))

(defn clear-polyline! []
  (when @!polyline
    (.setMap ^js @!polyline nil)
    (reset! !polyline nil)))

(defn set-polyline [gmap path]
  (let [polyline (create-polyline {:path path :color "#3b82f6" :opacity 0.75})]
    (clear-polyline!)
    (.setMap polyline gmap)
    (reset! !polyline polyline)
    polyline))


(defn set-polylines [gmap paths]
  (let [polylines (->> paths
                       (remove empty?)
                       (take 10)
                       (map-indexed vector)
                       (mapv
                        (fn [[idx path]]
                          (create-polyline {:path path
                                            :color (get-color idx)
                                            :opacity (max 0 (- 1 (/ idx 10)))}))))]
    (doseq [^js polyline polylines]
      (.setMap polyline gmap))
    polylines))

(defn clear-polylines [polylines]
  (doseq [^js polyline polylines]
    (.setMap polyline nil)))

(defn fit-bounds-to-path [^js gmap path]
  (let [bounds (js/google.maps.LatLngBounds.)]
    (if (seq path)
      (do
        (doseq [{:keys [lat lng]} path]
          (.extend bounds #js{:lat lat :lng lng}))
        (.fitBounds gmap bounds)
        (.panToBounds gmap bounds))
      (do (.setZoom gmap 2)
          (.setCenter gmap #js{:lat 0 :lng 0})))))
