(ns ui.hooks.use-map
  (:require
   [react :refer (useEffect)]
   [reagent.core :as r]
   [promesa.core :as p]
   [ui.subs :refer (listen)]
   [ui.lib.google.maps.core :refer (init-api)]
   [ui.lib.google.maps.polyline :refer (set-polylines clear-polylines)]
   [ui.lib.google.maps.marker :refer (set-markers clear-markers)]
   [ui.lib.google.maps.overlay :refer (set-overlays clear-overlays)]))

(def ^:private !el (atom nil))
(def ^:private !map (r/atom false))

(defn- center []
  (let [paths (listen [:map/paths])
        points (listen [:map/points])
        locations (listen [:map/locations])
        coords (flatten (concat
                         paths
                         (map :position points)
                         (map :position locations)))
        bounds (js/google.maps.LatLngBounds.)
        ^js gmap @!map]
    (if (seq coords)
      (do
        (doseq [{:keys [lat lng]} coords]
          (.extend bounds #js{:lat lat :lng lng}))
        (.fitBounds gmap bounds)
        (.panToBounds gmap bounds))
      (do (.setZoom gmap 2)
          (.setCenter gmap #js{:lat 0 :lng 0})))))

(defn use-map []
  (let [paths (listen [:map/paths])
        points (listen [:map/points])
        locations (listen [:map/locations])]

    (useEffect
     (fn []
       (p/let [gmap (init-api @!el)]
         (reset! !map gmap))
       #())
     #js[])

    (useEffect
     (fn []
       (if @!map
         (let [polylines (when (seq paths) (set-polylines @!map paths))
               markers (when (seq points) (set-markers @!map points))
               overlays (when (seq locations) (set-overlays @!map locations))]
           (center)
           #(do
              (clear-polylines polylines)
              (clear-markers markers)
              (clear-overlays overlays)))
         #()))
     #js[@!map paths points locations])

    {:ref !el
     :map @!map
     :center center}))
