(ns ui.hooks.use-map
  (:require
   [react :refer (useEffect)]
   [reagent.core :as r]
   [promesa.core :as p]
   [ui.subs :refer (listen)]
   [ui.lib.google.maps.core :refer (init-api)]
   [ui.lib.google.maps.polyline :refer (set-polylines clear-polylines)]
   [ui.lib.google.maps.marker :refer (set-markers clear-markers)]))

(def ^:private !el (atom nil))
(def ^:private !map (r/atom false))

(defn- center-route []
  (let [paths (listen [:map/paths])
        points (listen [:map/points])
        coords (flatten (concat paths (map :position points)))
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
        points (listen [:map/points])]

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
               markers (when (seq points) (set-markers @!map points))]
           (center-route)
           #(do
              (clear-polylines polylines)
              (clear-markers markers)
              (center-route)))
         #()))
     #js[@!map paths points])

    {:ref !el
     :map @!map
     :center center-route}))
