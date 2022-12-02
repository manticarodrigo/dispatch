(ns ui.lib.google.maps.directions
  (:require [ui.lib.google.maps.serializer :refer (parse-route)]
            [ui.lib.google.maps.marker :refer (clear-markers! create-marker!)]
            [cljs-bean.core :refer (->js)]))

(defonce ^:private !service (atom nil))

(defn- create-service []
  (js/google.maps.DirectionsService.))

(defn- create-route-request [waypoints]
  (let [stops (map (fn [stop] {:location (clj->js stop) :stopover true}) waypoints)]
    (clj->js {
              :origin (-> stops first :location ->js)
              :destination (-> stops last :location ->js)
              :waypoints (->> stops (drop-last 1) ->js)
              ;; :optimizeWaypoints true
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

(defn calc-route [waypoints]
  (js/Promise.
   (fn [resolve _]
     (let [^js service @!service
           request (create-route-request waypoints)]
       (.route service request
               (fn [response status]
                 (when (= status "OK")
                   (resolve (parse-route response)))))))))

(defn init-directions []
  (reset! !service (create-service)))
