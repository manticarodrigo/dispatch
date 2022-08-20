(ns app.hooks.use-map
  (:require
   [react :refer (useEffect)]
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [cljs.core.async :refer (go)]
   [cljs.core.async.interop :refer-macros (<p!)]
   [app.subs :refer (listen)]
   [app.utils.google.maps.core :refer (init-api)]
   [app.utils.google.maps.autocomplete :refer (search-places)]
   [app.utils.google.maps.places :refer (find-place)]
   [app.utils.google.maps.polyline :refer (set-polyline)]
   [app.utils.google.maps.overlay :refer (update-overlay)]
   [app.utils.google.maps.directions :refer (calc-route set-markers)]))

(set! *warn-on-infer* false)

(def ^:private !el (atom nil))
(def ^:private !map (r/atom false))

(defn- set-route! [origin stops]
  (go (dispatch [:route/set (<p! (calc-route origin stops))])))
(defn- set-origin! [place-id]
  (go (dispatch [:origin/set (<p! (find-place place-id))])))
(defn set-search! [text]
  (go (dispatch [:search/set (<p! (search-places text))])))

(defn use-map []
  (let [location (listen [:location])
        origin (listen [:origin])
        stops (listen [:stops])
        legs (listen [:route/legs])
        bounds (listen [:route/bounds])
        path (listen [:route/path])
        center-route #(do
                        (.fitBounds @!map (clj->js bounds))
                        (.panToBounds @!map (clj->js bounds)))]

    (useEffect
     (fn []
       (go (reset! !map (<p! (init-api @!el))))
       #()) #js[])

    (useEffect
     (fn []
       (when (and @!map location)
         (update-overlay (clj->js location)))
       #())
     #js[@!map location])

    (useEffect
     (fn []
       (when @!map
         (if (and (seq legs) (seq path))
           (do (set-markers @!map legs)
               (set-polyline @!map path)
               (center-route))
           (when (and origin (seq stops))
             (set-route! origin stops))))
       #())
     #js[@!map origin stops legs path])

    {:ref !el
     :map @!map
     :search set-search!
     :origin set-origin!
     :center center-route}))
