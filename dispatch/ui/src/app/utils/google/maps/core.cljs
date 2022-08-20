(ns app.utils.google.maps.core (:require
                                ["@googlemaps/js-api-loader" :refer (Loader)]
                                [clojure.core :refer (atom)]
                                [cljs.core.async :refer (go)]
                                [cljs.core.async.interop :refer-macros (<p!)]
                                [app.config :as config]
                                [app.subs :refer (listen)]
                                [app.utils.google.maps.styles :refer (styles)]))

(set! *warn-on-infer* false)

(defonce ^:private !loader (atom nil))

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

(defn init-map [el]
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

