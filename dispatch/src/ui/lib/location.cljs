(ns ui.lib.location
  (:require ["@capacitor/core" :refer (Capacitor registerPlugin)]))

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
         [location error]
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

(defn watch-position [cb]
  (let [platform (.getPlatform Capacitor)]
    (condp = platform
      "android" (watch-position-mobile cb)
      "ios" (watch-position-mobile cb)
      (.alert js/window "Location not supported on this platform."))))
