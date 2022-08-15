(ns app.hooks.use-route
  (:require
   [react]
   [re-frame.core :as rf]
   ["@googlemaps/js-api-loader" :refer (Loader)]
   ["@capacitor/geolocation" :refer (Geolocation)]
   [clojure.core :refer [atom]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [app.config :as config]
   [app.subs :as subs]
   [app.utils.logger :as logger]
   [app.utils.google-maps :refer (map-styles)]))

(defonce ^:private initial-zoom 4)
(defonce ^:private position-options
  (clj->js {:enableHighAccuracy true
            :timeout 10000
            :maximumAge 0}))

(defonce ^:private !loader (atom nil))
(defonce ^:private !el (atom nil))
(defonce ^:private !map (atom nil))
(defonce ^:private !markers (atom []))
(defonce ^:private !directions-service (atom nil))
(defonce ^:private !directions-renderer (atom nil))
(defonce ^:private !location-watch-id (atom nil))

(defn- load-map []
  (reset!
   !loader
   (Loader.
    (clj->js
     {:id "google-maps-script"
      :apiKey config/GOOGLE_MAPS_API_KEY
      :version "weekly"
      :language (subs/listen [:locale/language])
      :region (subs/listen [:locale/region])})))
  (.load @!loader))

(defn- create-map []
  (js/Promise.
   (fn [resolve _]
     (go
       (<p! (load-map))
       (resolve
        (js/google.maps.Map.
         @!el (clj->js {:center {:lat 0 :lng 0}
                        :zoom initial-zoom
                        :disableDefaultUI true
                        :styles map-styles})))))))

(defn- create-directions-service []
  (js/google.maps.DirectionsService.))

(defn- create-directions-renderer []
  (let [directions-renderer (js/google.maps.DirectionsRenderer.)]
    (.setMap directions-renderer @!map)
    directions-renderer))

(defn- create-lat-lng [lat lng]
  (js/google.maps.LatLng. lat lng))

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

(defn- clear-markers! []
  (doseq [^Marker marker @!markers] (.setMap marker nil))
  (reset! !markers []))

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

(defn- parse-position [pos]
  (let [coords (.-coords pos)
        lat (.-latitude coords)
        lng (.-longitude coords)]
    {:lat lat :lng lng}))

(defn- get-position []
  (-> (.getCurrentPosition
       Geolocation
       position-options)
      (.then #(rf/dispatch [:location/set (parse-position %)]))
      (.catch logger/error)))

(defn- watch-position []
  (go
    (reset!
     !location-watch-id
     (<p!
      (.watchPosition
       Geolocation
       position-options
       (fn [pos err]
         (if (some? pos)
           (rf/dispatch [:location/set (parse-position pos)])
           (logger/error err))))))))

(defn- clear-watch [id]
  (.clearWatch Geolocation #js{:id id}))

(defonce ^:private route-context (react/createContext {}))

(defn use-route-context []
  (let [val (react/useContext route-context)]
    (js->clj val :keywordize-keys true)))

(def route-context-provider (.-Provider route-context))

(defn use-route []
  (let [locale (subs/listen [:locale])
        location (subs/listen [:location])
        stops (subs/listen [:stops])]

    (react/useEffect
     (fn []
       (go
         (reset! !map (<p! (create-map)))
         (reset! !directions-service (create-directions-service))
         (reset! !directions-renderer (create-directions-renderer)))
       (fn []
         (when (some? @!location-watch-id)
           (logger/log "clearing watch id: " @!location-watch-id)
           (rf/dispatch [:location/set nil])
           (reset! !location-watch-id nil)
           (clear-watch @!location-watch-id))))
     #js[])

    (react/useEffect
     (fn []
       (when (some? location)
         (calc-route location stops))
       (fn []))
     #js[location])

    {:ref !el :get get-position :watch watch-position}))
