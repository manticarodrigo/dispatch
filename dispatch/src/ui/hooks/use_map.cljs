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
   [ui.lib.google.maps.polyline :refer (set-polyline
                                        clear-polyline!
                                        set-polylines
                                        clear-polylines
                                        fit-bounds-to-path)]
   [ui.lib.google.maps.marker :refer (clear-markers! fit-bounds-to-visible-markers)]
   [ui.lib.google.maps.overlay :refer (update-overlay)]
   [ui.lib.google.maps.directions :refer (set-markers)]))

(def ^:private !el (atom nil))
(def ^:private !map (r/atom false))

(defn- set-origin! [place-id]
  (go (dispatch [:origin/set (<p! (find-place place-id))])))
(defn- set-search! [text]
  (go (dispatch [:search/set (<p! (search-places text))])))

(defn- center-route []
  (let [^js gmap @!map]
    (fit-bounds-to-visible-markers gmap)))

(defn use-map []
  (let [paths (listen [:map/paths])
        location (listen [:location])
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
         (if legs
           (set-markers @!map legs)
           (clear-markers!))
         (if path
           (set-polyline @!map path)
           (clear-polyline!))
         (center-route))
       #())
     #js[@!map legs path])

    (useEffect
     (fn []
       (let [polylines (when (and @!map (seq paths)) (set-polylines @!map paths))]
         (when polylines (fit-bounds-to-path @!map (mapcat identity paths)))
         #(clear-polylines polylines)))
     #js[@!map paths])

    {:ref !el
     :map @!map
     :search set-search!
     :origin set-origin!
     :center center-route}))
