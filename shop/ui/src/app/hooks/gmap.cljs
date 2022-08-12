(ns app.hooks.gmap
  (:require
   [clojure.core :refer [atom]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [react]
   [re-frame.core :as rf]
   ["@googlemaps/js-api-loader" :refer (Loader)]
   ["@capacitor/geolocation" :refer (Geolocation)]
   [app.config :as config]
   [app.subs :as subs]
   [app.utils.gmap :refer (map-styles)]))


(defonce ^:private loader
  (Loader.
   (clj->js
    {:apiKey config/GOOGLE_MAPS_API_KEY
     :version "weekly"})))

(defonce ^:private initial-zoom 4)

(defonce ^:private !el (atom nil))
(defonce ^:private !map (atom nil))
(defonce ^:private !directions-service (atom nil))
(defonce ^:private !directions-renderer (atom nil))
(defonce ^:private !location-watch-id (atom nil))

(defn- create-map [el]
  (new js/Promise
       (fn [resolve _]
         (go
           (<p! (.load loader))
           (resolve
            (js/google.maps.Map.
             el (clj->js {:center {:lat 0 :lng 0}
                          :zoom initial-zoom
                          :disableDefaultUI true
                          :styles map-styles})))))))

(defn- create-directions-service []
  (js/google.maps.DirectionsService.))

(defn- create-directions-renderer [map-instance]
  (let [directions-renderer (js/google.maps.DirectionsRenderer.)]
    (.setMap directions-renderer map-instance)
    directions-renderer))

(defn- create-lat-lng [lat lng]
  (js/google.maps.LatLng. lat lng))

(defn- calc-route [^DirectionsService service
                   ^DirectionsRenderer renderer
                   start
                   end]
  (let [request #js{:origin start
                    :destination end
                    :travelMode "DRIVING"}]
    (.route service request
            (fn [response status]
              (when (= status "OK")
                (.setDirections renderer response))))))

(defn- handle-position-change [pos err]
  (go
    (if (some? pos)
      (let [coords (.-coords pos)
            lat (.-latitude coords)
            lng (.-longitude coords)
            lat-lng {:lat lat :lng lng}]
        (rf/dispatch [:location/update lat-lng]))
      (js/console.log err))))

(defn- watch-position [cb]
  (.watchPosition
   Geolocation
   (clj->js {:enableHighAccuracy true
             :timeout 10000
             :maximumAge 0})
   cb))

(defn- clear-watch [id]
  (.clearWatch Geolocation #js{:id id}))

(defn hook []
  (let [location (subs/listen [:location/current])
        ref-location (subs/listen [:ref-location/current])
        {ref-lat :lat ref-lng :lng} ref-location]

    (react/useEffect
     (fn []
       (go
         (reset! !map (<p! (create-map @!el)))
         (reset! !directions-service (create-directions-service))
         (reset! !directions-renderer (create-directions-renderer @!map))
         (reset! !location-watch-id (<p! (watch-position handle-position-change)))
         (js/console.log "added watch id: " @!location-watch-id))

       (fn []
         (js/console.log "clearing watch id: " @!location-watch-id)
         (rf/dispatch [:location/update nil])
         (reset! !location-watch-id nil)
         (clear-watch @!location-watch-id)))
     #js[])

    (react/useEffect
     (fn []
       (when-let [{lat :lat lng :lng} location]
         (calc-route
          @!directions-service
          @!directions-renderer
          (create-lat-lng ref-lat ref-lng)
          (create-lat-lng lat lng)))
       (fn []))
     #js[location])

    !el))
