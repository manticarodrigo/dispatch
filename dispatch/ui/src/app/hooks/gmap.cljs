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
   [app.utils.gmap :refer (map-styles)]
   [app.utils.logger :as logger]))


(defonce ^:private loader
  (Loader.
   (clj->js
    {:apiKey config/GOOGLE_MAPS_API_KEY
     :version "weekly"})))

(defonce ^:private initial-zoom 4)

(defonce ^:private !el (atom nil))
(defonce ^:private !map (atom nil))
(defonce ^:private !markers (atom []))
(defonce ^:private !directions-service (atom nil))
(defonce ^:private !directions-renderer (atom nil))
(defonce ^:private !location-watch-id (atom nil))

(defn- create-map [el]
  (js/Promise.
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

(defn- clear-markers! []
  (doseq [^Marker marker @!markers] (.setMap marker nil))
  (reset! !markers []))

(defn- get-lat-lng [location]
  (let [{lat :lat lng :lng} location]
    (create-lat-lng lat lng)))

(defn- create-route-request [location stops]
  (let [origin (get-lat-lng location)
        waypoints (map (fn [stop] {:location (get-lat-lng stop) :stopover true}) stops)]
    (clj->js {:origin origin
              :destination origin
              :waypoints waypoints
              :optimizeWaypoints true
              :travelMode "DRIVING"})))

(defn- parse-leg [leg]
  (let [{:keys [distance duration end_address]} (js->clj leg :keywordize-keys true)]
    {:distance distance :duration duration :address end_address}))

(defn- parse-legs [^DirectionsResult response]
  (some-> response (.-routes) (first) (.-legs)))

(defn- parse-route [legs]
  (mapv parse-leg legs))

(defn- create-route-markers [legs]
  (clear-markers!)
  (doseq [[idx leg] (map-indexed vector legs)]
    (let [position (.-end_location leg)
          title (.-end_address leg)
          label (str (+ 1 idx))
          last? (->> legs (count) (= (+ 1 idx)))
          marker (js/google.maps.Marker.
                  (clj->js {:position position
                            :title title
                            :label (when-not last? {:text label :color "white"})
                            :icon (if last? {:url "/icons/material/flag.svg"
                                             :anchor (js/google.maps.Point. 5 25)}
                                      "/icons/material/pin.svg")
                            :map @!map}))]
      (swap! !markers conj marker))))

(defn- handle-route-response [^DirectionsResult response]
  (let [^DirectionsRenderer renderer @!directions-renderer
        legs (parse-legs response)
        route (parse-route legs)]
    (.setOptions renderer #js{:suppressMarkers true})
    (.setDirections renderer response)
    (create-route-markers legs)
    (rf/dispatch [:route/set route])))

(defn- calc-route [location stops]
  (let [^DirectionsService service @!directions-service
        request (create-route-request location stops)]
    (.route service request
            (fn [response status]
              (when (= status "OK")
                (handle-route-response response))))))

(defn- handle-position-change [pos err]
  (go
    (if (some? pos)
      (let [coords (.-coords pos)
            lat (.-latitude coords)
            lng (.-longitude coords)
            lat-lng {:lat lat :lng lng}]
        (rf/dispatch [:location/set lat-lng]))
      (logger/error err))))

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
        stops (subs/listen [:stops/current])]

    (react/useEffect
     (fn []
       (go
         (reset! !map (<p! (create-map @!el)))
         (reset! !directions-service (create-directions-service))
         (reset! !directions-renderer (create-directions-renderer @!map))
         (reset! !location-watch-id (<p! (watch-position handle-position-change)))
         (logger/log "added watch id: " @!location-watch-id))

       (fn []
         (logger/log "clearing watch id: " @!location-watch-id)
         (rf/dispatch [:location/set nil])
         (reset! !location-watch-id nil)
         (clear-watch @!location-watch-id)))
     #js[])

    (react/useEffect
     (fn []
       (when (some? location)
         (calc-route location stops))
       (fn []))
     #js[location])

    !el))
