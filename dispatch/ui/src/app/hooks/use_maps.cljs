(ns app.hooks.use-maps
  (:require
   [react]
   [re-frame.core :as rf]
   ["@googlemaps/js-api-loader" :refer (Loader)]
   [clojure.core :refer [atom]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [app.config :as config]
   [app.subs :as subs]
   [app.utils.google-maps :refer (map-styles)]
   [app.utils.i18n :refer (tr)]))

(defonce ^:private initial-zoom 4)

(defonce ^:private !loader (atom nil))
(defonce ^:private !el (atom nil))
(defonce ^:private !map (atom nil))
(defonce ^:private !route-markers (atom []))
(defonce ^:private !location-marker (atom nil))
(defonce ^:private !directions-service (atom nil))
(defonce ^:private !directions-renderer (atom nil))

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

(defn- create-route-request [origin stops]
  (let [origin (get-lat-lng origin)
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

(defn- create-marker [opts]
  (js/google.maps.Marker.
   (clj->js (assoc opts :map @!map))))

(defn- clear-markers! []
  (doseq [^Marker marker @!route-markers] (.setMap marker nil))
  (reset! !route-markers []))

(defn- create-route-markers [legs]
  (clear-markers!)
  (doseq [[idx leg] (map-indexed vector legs)]
    (let [position (.-end_location leg)
          title (.-end_address leg)
          label (str (+ 1 idx))
          last? (->> legs (count) (= (+ 1 idx)))
          marker (create-marker
                  {:position position
                   :title title
                   :label (when-not last? {:text label :color "white"})
                   :icon (if last?
                           {:url "/icons/route/shop.png"
                            :scaledSize (js/google.maps.Size. 35 35)}
                           {:url "/icons/material/pin.svg"})
                   :zIndex idx})]
      (swap! !route-markers conj marker))))

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

(defonce ^:private route-context (react/createContext {}))

(defn use-route-context []
  (let [val (react/useContext route-context)]
    (js->clj val :keywordize-keys true)))

(def route-context-provider (.-Provider route-context))

(defn use-maps []
  (let [location (subs/listen [:location])
        origin (subs/listen [:origin])
        stops (subs/listen [:stops])]

    (react/useEffect
     (fn []
       (go
         (reset! !map (<p! (create-map)))
         (reset! !directions-service (create-directions-service))
         (reset! !directions-renderer (create-directions-renderer)))
       #())
     #js[])

    (react/useEffect
     (fn []
       (when (some? location)
         (if-let [marker @!location-marker]
           (.setPosition marker (get-lat-lng location))
           (reset!
            !location-marker
            (create-marker
             {:position (get-lat-lng location)
              :title (tr [:location/title])
              :icon {:url "/icons/route/delivery.png"
                     :scaledSize (js/google.maps.Size. 35 35)}
              :zIndex (count @!route-markers)}))))
       (fn []))
     #js[location])

    (react/useEffect
     (fn []
       (when (some? origin)
         (calc-route origin stops))
       (fn []))
     #js[origin])

    !el))
