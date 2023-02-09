(ns ui.lib.google.maps.marker
  (:require [cljs-bean.core :refer (->js)]))

(defn create-marker [options]
  (js/google.maps.Marker. (->js options)))

(defn clear-marker [^js marker]
  (.setMap marker nil))

(defn set-markers [^js gmap points]
  (let [markers (mapv
                 (fn [[idx {:keys [position title]}]]
                   (create-marker
                    {:map gmap
                     :zIndex idx
                     :position position
                     :label {:text title
                             :fontSize "0.875rem"
                             :className "font-sans text-base text-sm text-neutral-900"}
                     :icon {:url "/images/svg/pin.svg"
                            :scaledSize (js/google.maps.Size. 30 30)
                            :labelOrigin (js/google.maps.Point. 15 -6)}}))
                 (map-indexed vector points))]
    (doseq [^js marker markers]
      (.setMap marker gmap))
    markers))

(defn clear-markers [markers]
  (doseq [marker markers]
    (clear-marker marker)))
