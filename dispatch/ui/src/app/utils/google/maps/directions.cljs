(ns app.utils.google.maps.directions
  (:require [app.utils.google.maps.serializer :refer (parse-route)]
            [app.utils.google.maps.marker :refer (clear-markers! create-marker!)]))

(defonce ^:private !service (atom nil))

(defn- create-service []
  (js/google.maps.DirectionsService.))

(defn- create-route-request [origin stops]
  (let [origin (clj->js origin)
        waypoints (map (fn [stop] {:location (clj->js stop) :stopover true}) stops)]
    (clj->js {:origin origin
              :destination origin
              :waypoints waypoints
              :optimizeWaypoints true
              :travelMode "DRIVING"})))

(defn set-markers [gmap legs]
  (clear-markers!)
  (doseq [[idx leg] (map-indexed vector legs)]
    (create-marker!
     {:map gmap
      :zIndex idx
      :position (clj->js (:location leg))
      :title (:address leg)
      :label {:text (str (+ 1 idx))
              :color "white"}
      :icon {:url "/images/svg/pin.svg"
             :scaledSize (js/google.maps.Size. 30 30)}})))

(defn calc-route [location stops]
  (js/Promise.
   (fn [resolve _]
     (let [^js service @!service
           request (create-route-request location stops)]
       (.route service request
               (fn [response status]
                 (when (= status "OK")
                   (resolve (parse-route response)))))))))

(defn init-directions []
  (reset! !service (create-service)))