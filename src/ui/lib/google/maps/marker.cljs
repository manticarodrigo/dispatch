(ns ui.lib.google.maps.marker
  (:require ["@googlemaps/markerclusterer" :refer (MarkerClusterer)]
            [reagent.dom.server :refer (render-to-string)]
            [cljs-bean.core :refer (->js)]))

(def !clusterer (atom nil))

(defn create-marker [options]
  (js/google.maps.Marker. (->js options)))

(defn render-marker [color]
  (str
   "data:image/svg+xml;base64,"
   (js/window.btoa
    (render-to-string
     [:svg {:xmlns "http://www.w3.org/2000/svg"
            :width 30
            :height 30
            :viewBox "0 0 20 20"
            :fill color}
      [:path {:fill-rule "evenodd"
              :d "M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 NaN"
              :clip-rule "evenodd"}]]))))

(defn render [^js cluster ^js stats]
  (let [count (.. cluster -count)
        position (.. cluster -position)
        mean (.. stats -clusters -markers -mean)
        color (if (> count (max 10 mean)) "#7f1d1d" "#b91c1c")]
    (create-marker {:zIndex count
                    :position position
                    :label {:text (str count)
                            :color "#fff"
                            :fontSize "0.875rem"}
                    :icon {:url (render-marker color)
                           :scaledSize (js/google.maps.Size. 40 40)}})))

(defn set-markers [^js gmap points]
  (let [markers (->js
                 (mapv
                  (fn [[idx {:keys [position title]}]]
                    (create-marker
                     {:map gmap
                      :zIndex idx
                      :position position
                      :label {:text title
                              :fontSize "0.875rem"}
                      :icon {:url (render-marker "#ef4444")
                             :scaledSize (js/google.maps.Size. 30 30)
                             :labelOrigin (js/google.maps.Point. 15 -6)}}))
                  (map-indexed vector points)))
        ^js clusterer @!clusterer]
    (if clusterer
      (do
        (.setMap clusterer gmap)
        (.addMarkers clusterer markers))
      (reset!
       !clusterer
       (MarkerClusterer.
        (->js {:map gmap
               :markers markers
               :renderer {:render render}}))))
    markers))

(defn clear-markers [_]
  (when-let [^js clusterer @!clusterer]
    (.clearMarkers clusterer)))
