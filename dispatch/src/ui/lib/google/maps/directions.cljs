(ns ui.lib.google.maps.directions
  (:require [ui.lib.google.maps.serializer :refer (parse-route)]
            [cljs-bean.core :refer (->js)]))

(defonce ^:private !service (atom nil))

(defn- create-service []
  (js/google.maps.DirectionsService.))

(defn- create-route-request [places options]
  (let [stops (map (fn [%] {:location % :stopover true}) places)]
    (->js (merge {:origin (-> stops first :location ->js)
                  :destination (-> stops last :location ->js)
                  :waypoints (->> stops (drop-last 1) ->js)
                  :travelMode "DRIVING"}
                 options))))

(defn calc-route [places]
  (js/Promise.
   (fn [resolve _]
     (let [^js service @!service
           request (create-route-request places {})]
       (.route service request
               (fn [response status]
                 (when (= status "OK")
                   (resolve (parse-route response)))))))))

(defn calc-optimized-route [places]
  (js/Promise.
   (fn [resolve _]
     (let [^js service @!service
           request (create-route-request places {:optimizeWaypoints true})]
       (.route service request
               (fn [response status]
                 (when (= status "OK")
                   (resolve (parse-route response)))))))))

(defn init-directions []
  (reset! !service (create-service)))
