(ns ui.lib.google.maps.core
  (:require
   ["@googlemaps/js-api-loader" :refer (Loader)]
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [ui.config :as config]
   [ui.subs :refer (listen)]
   [ui.lib.google.maps.styles :refer (styles)]
   [ui.lib.google.maps.autocomplete :refer (init-autocomplete)]
   [ui.lib.google.maps.places :refer (init-places)]
   [ui.lib.google.maps.directions :refer (init-directions)]
   [ui.lib.google.maps.geocoding :refer (init-geocoding)]))

(defn- load-map []
  (let [loader (Loader.
                (->js
                 {:id "google-maps-script"
                  :apiKey config/GOOGLE_API_KEY
                  :version "weekly"
                  :libraries ["places"]
                  :language (listen [:language])}))]
    (.load loader)))

(defn- init-map [el]
  (p/do
    (load-map)
    (js/google.maps.Map.
     el (->js {:center {:lat 0 :lng 0}
               :zoom 2
               :disableDefaultUI true
               :styles (:caen styles)}))))

(defn init-api [el]
  (p/let [gmap (init-map el)]
    (p/do
      (init-places gmap)
      (init-directions)
      (init-autocomplete)
      (init-geocoding)
      gmap)))
