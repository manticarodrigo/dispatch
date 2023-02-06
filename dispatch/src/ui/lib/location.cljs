(ns ui.lib.location
  (:require ["@capacitor/core" :refer (Capacitor registerPlugin)]
            [promesa.core :as p]))

(def BackgroundGeolocation (registerPlugin "BackgroundGeolocation"))

(defn- watch-position-mobile [cb]
  (-> BackgroundGeolocation
      (.addWatcher
       #js
        {:backgroundMessage "Cancel to prevent battery drain.",
         :backgroundTitle "Tracking You.",
         :requestPermissions true,
         :stale false,
         :distanceFilter 50}
       (fn callback
         [^js location ^js error]
         (if error
           (do
             (when (= (.-code error) "NOT_AUTHORIZED")
               (when
                (.confirm
                 js/window
                 (str
                  "This app needs your location, "
                  "but does not have permission.\n\n"
                  "Open settings now?"))
                 (.openSettings BackgroundGeolocation)))
             (.error js/console error))
           (cb {:latitude (.-latitude location)
                :longitude (.-longitude location)
                :accuracy (.-accuracy location)
                :altitude (.-altitude location)
                :altitudeAccuracy (.-altitudeAccuracy location)
                :bearing (.-bearing location)
                :speed (.-speed location)
                :timestamp (.-timestamp location)}))))
      (.then (fn [watcher-id]
               #(.removeWatcher BackgroundGeolocation #js{:id watcher-id})))))

(defn watch-position-web [cb]
  (p/resolved
   (let [watch-id (-> js/navigator.geolocation
                      (.watchPosition
                       (fn [^js position]
                         (let [coords (.-coords position)]
                           (cb {:latitude (.-latitude coords)
                                :longitude (.-longitude coords)
                                :accuracy (.-accuracy coords)
                                :altitude (.-altitude coords)
                                :altitudeAccuracy (.-altitudeAccuracy coords)
                                :bearing (.-heading coords)
                                :speed (.-speed coords)
                                :timestamp (.-timestamp position)})))
                       (fn [error]
                         (.error js/console error))
                       #js{:enableHighAccuracy true
                           :timeout 10000
                           :maximumAge 0}))]

     (fn [] (.clearWatch js/navigator.geolocation watch-id)))))

(defn watch-position [cb]
  (let [platform (.getPlatform Capacitor)]
    (condp = platform
      "android" (watch-position-mobile cb)
      "ios" (watch-position-mobile cb)
      "web" (watch-position-web cb)
      (.alert js/window "Location not supported on this platform."))))
