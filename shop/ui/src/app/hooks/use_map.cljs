(ns app.hooks.use-map
  (:require
   [clojure.set :refer [rename-keys]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [react]
   [re-frame.core :as rf]
   ["@googlemaps/js-api-loader" :refer (Loader)]
   ["@capacitor/geolocation" :refer (Geolocation)]
   [app.config :as config]))


(def loader (Loader.
             (clj->js
              {:apiKey (config/env :google-maps-api-key)
               :version "weekly"})))

(defn- create-map [el opts]
  (new js/Promise
       (fn [resolve _]
         (go
           (<p! (.load loader))
           (resolve
            (js/google.maps.Map.
             el (clj->js (conj opts {:disableDefaultUI true
                                     :zoomControl true}))))))))

(defn- create-lat-lng [lat lng]
  (js/google.maps.LatLng. lat lng))

(defn- create-marker [map name lat lng]
  (js/google.maps.Marker. (clj->js
                           {:map map
                            :position (create-lat-lng lat lng)
                            :title name})))

(defn- move-marker [marker lat lng]
  (.setPosition marker (create-lat-lng lat lng)))

(defn- handle-position-change [^GoogleMap map-instance]
  (let [!location-marker (clojure.core/atom nil)]
    (fn [pos err]
      (go
        (if (some? pos)
          (let [coords (.-coords pos)
                lat (.-latitude coords)
                lng (.-longitude coords)]
            (.setCenter map-instance #js{:lat lat :lng lng})
            (.setZoom map-instance 12)

            (rf/dispatch [:location/update {:coords {:lat lat :lng lng}
                                            :zoom 12}])

            (if (some? @!location-marker)
              (move-marker @!location-marker lat lng)
              (reset! !location-marker (create-marker map-instance "My location" lat lng))))

          (js/console.log err))))))

;; (defn- request-permissions []
;;   (go (let [permissions (<p! (.checkPermissions Geolocation))]
;;         (js/console.log permissions)
;;         (when-not (= (.-location permissions) "granted")
;;           (try
;;             (<p! (.requestPermissions Geolocation))
;;             (catch js/Error err (js/console.log err)))))))

;; (defn- get-position []
;;   (go (let [coordinates (<p! (.getCurrentPosition Geolocation))]
;;         (js/console.log coordinates))))

(defn- watch-position [cb]
  (.watchPosition
   Geolocation
   (clj->js {:enableHighAccuracy true
             :timeout 10000
             :maximumAge 0})
   cb))

(defn hook []
  (let [!map-el (clojure.core/atom nil)
        !map-instance (clojure.core/atom nil)
        !location-watch-id (clojure.core/atom nil)
        !location (rf/subscribe [:location/current])]
    (react/useEffect
     (fn []
       (go
         (reset! !map-instance (<p! (create-map @!map-el (rename-keys @!location {:coords :center}))))
         (reset! !location-watch-id (<p! (watch-position (handle-position-change @!map-instance))))
         (js/console.log "watch id: " @!location-watch-id)
         (js/console.log "map instance: " @!map-instance))
       (fn []))
     #js[])
    !map-el))
