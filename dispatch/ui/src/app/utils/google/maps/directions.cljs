(ns app.utils.google.maps.directions
  (:require [app.utils.google.maps.marker :refer (clear-markers! create-marker!)]))

(set! *warn-on-infer* false)

(defonce ^:private !service (atom nil))

(defn- create-service []
  (js/google.maps.DirectionsService.))

(defn init-directions []
  (reset! !service (create-service)))

(defn- create-lat-lng [lat lng]
  (js/google.maps.LatLng. lat lng))

(defn get-lat-lng [location]
  (let [{lat :lat lng :lng} location]
    (create-lat-lng lat lng)))

(defn parse-lat-lng [lat-lng]
  (let [lat (.lat lat-lng)
        lng (.lng lat-lng)]
    {:lat lat :lng lng}))

(defn- create-route-request [origin stops]
  (let [origin (get-lat-lng origin)
        waypoints (map (fn [stop] {:location (get-lat-lng stop) :stopover true}) stops)]
    (clj->js {:origin origin
              :destination origin
              :waypoints waypoints
              :optimizeWaypoints true
              :travelMode "DRIVING"})))

(defn parse-leg [leg]
  (let [{:keys [distance
                duration
                end_address
                end_location]} (js->clj leg :keywordize-keys true)]
    {:distance distance
     :duration duration
     :address end_address
     :location (parse-lat-lng end_location)}))

(defn set-markers [gmap legs]
  (clear-markers!)
  (doseq [[idx leg] (map-indexed vector (map parse-leg legs))]
    (create-marker!
     {:map gmap
      :zIndex idx
      :position (get-lat-lng (:location leg))
      :title (:address leg)
      :label {:text (str (+ 1 idx))
              :color "white"}
      :icon {:url "/images/svg/pin.svg"
             :scaledSize (js/google.maps.Size. 30 30)}})))

(defn calc-route [location stops]
  (js/Promise.
   (fn [resolve _]
     (let [service @!service
           request (create-route-request location stops)]
       (.route service request
               (fn [response status]
                 (when (= status "OK")
                   (resolve (some-> response .-routes first)))))))))
