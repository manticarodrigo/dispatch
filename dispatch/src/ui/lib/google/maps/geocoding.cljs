(ns ui.lib.google.maps.geocoding
  (:require [cljs-bean.core :refer (->js ->clj)]))

(defonce ^:private !geocoding-service (atom nil))

(defn reverse-geocode [lat-lng]
  (-> ^js @!geocoding-service
      (.geocode (->js {:location lat-lng}))
      (.then ->clj)))

(defn init-geocoding []
  (reset! !geocoding-service (js/google.maps.Geocoder.)))
