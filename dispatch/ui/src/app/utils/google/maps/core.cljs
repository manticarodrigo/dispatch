(ns app.utils.google.maps.core
  (:require
   ["@googlemaps/js-api-loader" :refer (Loader)]
   [clojure.core :refer (atom)]
   [cljs.core.async :refer (go)]
   [cljs.core.async.interop :refer-macros (<p!)]
   [app.config :as config]
   [app.subs :refer (listen)]
   [app.utils.google.maps.styles :refer (styles)]
   [app.utils.google.maps.autocomplete :refer (init-autocomplete)]
   [app.utils.google.maps.places :refer (init-places)]
   [app.utils.google.maps.overlay :refer (init-overlay)]
   [app.utils.google.maps.directions :refer (init-directions)]))

(set! *warn-on-infer* false)

(defonce ^:private !loader (atom nil))
(defonce ^:private !map (atom nil))

(defn- load-map []
  (reset!
   !loader
   (Loader.
    (clj->js
     {:id "google-maps-script"
      :apiKey config/GOOGLE_MAPS_API_KEY
      :version "weekly"
      :libraries ["places"]
      :language (listen [:locale/language])
      :region (listen [:locale/region])})))
  (.load @!loader))

(defn- init-map [el]
  (js/Promise.
   (fn [resolve _]
     (go
       (<p! (load-map))
       (resolve
        (js/google.maps.Map.
         el (clj->js {:center {:lat 0 :lng 0}
                      :zoom 2
                      :disableDefaultUI true
                      :styles (:caen styles)})))))))

(defn init-api [el]
  (js/Promise.
   (fn [resolve _]
     (go
       (reset! !map (<p! (init-map el)))
       (init-directions)
       (init-overlay @!map)
       (init-autocomplete)
       (init-places @!map)
       (resolve @!map)))))
