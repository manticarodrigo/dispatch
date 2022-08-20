(ns app.hooks.use-map
  (:require
   [react :refer (useEffect)]
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [cljs.core.async :refer (go)]
   [cljs.core.async.interop :refer-macros (<p!)]
   [app.subs :refer (listen)]
   [app.utils.google.maps.core :refer (init-map)]
   [app.utils.google.maps.autocomplete :refer (init-autocomplete search-places)]
   [app.utils.google.maps.places :refer (init-places find-place)]
   [app.utils.google.maps.polyline :refer (set-polyline)]
   [app.utils.google.maps.overlay :refer (init-overlay update-overlay)]
   [app.utils.google.maps.directions :refer (init-directions
                                             calc-route
                                             set-markers
                                             parse-lat-lng
                                             get-lat-lng)]))

(set! *warn-on-infer* false)

(def ^:private !ready? (r/atom false))

(defonce ^:private !el (atom nil))
(defonce ^:private !map (atom nil))

(defn- set-origin! [place-id]
  (go
    (let [place (<p! (find-place place-id))]
      (dispatch
       [:origin/set (parse-lat-lng (-> place .-geometry .-location))]))))

(defn set-search! [text]
  (go
    (let [results (<p! (search-places text))]
      (dispatch
       [:search/set (js->clj results :keywordize-keys true)]))))

(defn- init-api []
  (go
    (reset! !map (<p! (init-map @!el)))
    (init-directions)
    (init-overlay @!map)
    (init-autocomplete)
    (init-places @!map)
    (reset! !ready? true)))

(defn use-map []
  (let [ready? @!ready?
        location (listen [:location])
        origin (listen [:origin])
        stops (listen [:stops])
        route (listen [:route])
        legs (some-> route .-legs)
        bounds (listen [:route/bounds])
        center-route #(do
                        (.fitBounds @!map bounds)
                        (.panToBounds @!map bounds))]

    (useEffect #(init-api) #js[])

    (useEffect
     (fn []
       (when (and ready? location)
         (update-overlay (get-lat-lng location)))
       #())
     #js[ready? location])

    (useEffect
     (fn []
       (when ready?
         (if (seq legs)
           (do (set-markers @!map legs)
               (set-polyline @!map (.-overview_path route))
               (center-route))
           (when (and origin (seq stops))
             (go (dispatch [:route/set (<p! (calc-route origin stops))])))))
       #())
     #js[ready? origin stops legs route])

    {:ref !el
     :map @!map
     :search set-search!
     :origin set-origin!
     :center center-route}))
