(ns ui.lib.google.maps.polyline
  (:require [cljs-bean.core :refer (->js)]
            [ui.utils.color :refer (get-color)]))

(defn create-polyline [{:keys [path color opacity]}]
  (js/google.maps.Polyline.
   (->js {:path path
          :geodisic true
          :strokeColor color
          :strokeOpacity opacity
          :strokeWeight 6})))

(defn clear-polyline [^js polyline]
  (.setMap polyline nil))

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
  (doseq [polyline polylines]
    (clear-polyline polyline)))

(defn decode-polyline [encoded-polyline]
  (when-let [decode (.. js/window -google -maps -geometry -encoding -decodePath)]
    (->> (decode encoded-polyline)
         (mapv (fn [latlng]
                 (let [lat (.lat latlng)
                       lng (.lng latlng)]
                   {:lat lat :lng lng}))))))

(defn decode-polylines [encoded-polylines]
  (mapv decode-polyline encoded-polylines))
