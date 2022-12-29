(ns ui.hooks.use-map
  (:require
   [react :refer (useEffect)]
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [cljs.core.async :refer (go)]
   [cljs.core.async.interop :refer-macros (<p!)]
   [cljs-bean.core :refer (->js)]
   [ui.subs :refer (listen)]
   [ui.lib.google.maps.core :refer (init-api)]
   [ui.lib.google.maps.autocomplete :refer (search-places)]
   [ui.lib.google.maps.places :refer (find-place)]
   [ui.lib.google.maps.polyline :refer (set-polyline clear-polyline!)]
   [ui.lib.google.maps.marker :refer (clear-markers!)]
   [ui.lib.google.maps.overlay :refer (update-overlay)]
   [ui.lib.google.maps.directions :refer (calc-route set-markers)]))

(def ^:private !el (atom nil))
(def ^:private !map (r/atom false))

(defn- set-route! [waypoints]
  (go (dispatch [:route/set (<p! (calc-route waypoints))])))
(defn- set-origin! [place-id]
  (go (dispatch [:origin/set (<p! (find-place place-id))])))
(defn- set-search! [text]
  (go (dispatch [:search/set (<p! (search-places text))])))

(defn- center-route []
  (let [^js gmap @!map
        bounds (listen [:route/bounds])]
    (if bounds
      (do (.fitBounds gmap (clj->js bounds))
          (.panToBounds gmap (clj->js bounds)))
      (do (.setZoom gmap 2)
          (.setCenter gmap (->js {:lat 0 :lng 0}))))))

(defn use-map []
  (let [location (listen [:location])
        stops (listen [:stops])
        legs (listen [:route/legs])
        path (listen [:route/path])]

    (useEffect
     (fn []
       (go (reset! !map (<p! (init-api @!el))))
       #()) #js[])

    (useEffect
     (fn []
       (when (and @!map location)
         (update-overlay (->js location)))
       #())
     #js[@!map location])

    (useEffect
     (fn []
       (when @!map
         (center-route)
         (if legs
           (set-markers @!map legs)
           (clear-markers!))
         (if path
           (set-polyline @!map path)
           (clear-polyline!)))
       #())
     #js[@!map legs path])

    (useEffect
     (fn []
       (when (and @!map (> (count stops) 1))
         (set-route! stops))
       #())
     #js[@!map stops])

    {:ref !el
     :map @!map
     :search set-search!
     :origin set-origin!
     :center center-route}))
