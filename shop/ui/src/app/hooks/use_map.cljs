(ns app.hooks.use-map
  (:require
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [react]
   [re-frame.core :as rf]
   [app.config :as config]
   [app.subs]
   ["@capacitor/google-maps" :refer (GoogleMap)]
   ["@capacitor/geolocation" :refer (Geolocation)]))


(defn- create-map [element config]
  (.create GoogleMap
           (clj->js {:id "portal-map"
                     :element element
                     :apiKey (config/env :google-maps-api-key)
                     :config config})))

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

(defn- handle-position-change [^GoogleMap map-instance]
  (let [!location-marker-id (clojure.core/atom nil)]
    (fn [pos err]
      (go
        (if (some? pos)
          (let [coords (.-coords pos)
                lat (.-latitude coords)
                lng (.-longitude coords)
                opts {:coordinate
                      {:lat lat :lng lng}}]
            (js/console.log #js{:lat lat :lng lng})

            (.setCamera map-instance (clj->js
                                      opts))
            ;; (js/console.log "location marker id: " @!location-marker-id)
            ;; (when-let [id @!location-marker-id]
            ;;   (reset! !location-marker-id nil)
            ;;   (js/console.log "clearing marker: " id)
            ;;   (<p! (.removeMarker
            ;;         map-instance
            ;;         @!location-marker-id)))
            ;; (reset!
            ;;  !location-marker-id
            ;;  (<p! (.addMarker
            ;;        map-instance
            ;;        (clj->js
            ;;         (assoc opts :title "Your position")))))
            )
          (js/console.log err))))))

(defn hook []
  (let [!map-el (clojure.core/atom nil)
        !map-instance (clojure.core/atom nil)
        !location-watch-id (clojure.core/atom nil)
        !location (rf/subscribe [:location/current])]
    (react/useEffect
     (fn []
       (go
         (reset! !map-instance (<p! (create-map @!map-el @!location)))
         (reset! !location-watch-id (<p! (watch-position (handle-position-change @!map-instance))))
         (js/console.log "watch id: " @!location-watch-id)
         (js/console.log "map: " @!map-instance))
       (fn [] (.destroy @!map-instance)))
     #js[])
    !map-el))
