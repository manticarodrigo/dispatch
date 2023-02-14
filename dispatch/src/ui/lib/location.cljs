(ns ui.lib.location
  (:require ["@capacitor/core" :refer (Capacitor registerPlugin)]
            [promesa.core :as p]
            [ui.utils.i18n :refer [tr]]))

(def BackgroundGeolocation (registerPlugin "BackgroundGeolocation"))

(defn- watch-position-mobile [cb]
  (-> BackgroundGeolocation
      (.addWatcher
       #js
        {:backgroundTitle (tr [:location/title]),
         :backgroundMessage (tr [:location/message]),
         :requestPermissions true,
         :stale false,
         :distanceFilter 50}
       (fn callback
         [^js location ^js error]
         (if error
           (do
             (when (= (.-code error) "NOT_AUTHORIZED")
               (when
                (.confirm js/window (tr [:location/permission]))
                 (.openSettings BackgroundGeolocation)))
             (.error js/console error))
           (cb {:latitude (.-latitude location)
                :longitude (.-longitude location)
                :accuracy (.-accuracy location)
                :altitude (.-altitude location)
                :altitudeAccuracy (.-altitudeAccuracy location)
                :heading (.-bearing location)
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
                                :heading (.-heading coords)
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
      (.alert js/window (tr [:location/unsupported])))))

(defn get-location []
  (p/let [platform (.getPlatform Capacitor)
          !last-location (atom nil)
          cb #(reset! !last-location %)]
    (-> (condp = platform
          "android" (watch-position-mobile cb)
          "ios" (watch-position-mobile cb)
          "web" (watch-position-web cb)
          (.alert js/window (tr [:location/unsupported])))
        (.then (fn [clear-watch]
                 (-> (p/delay 500)
                     (p/then #(do (clear-watch)
                                  @!last-location))))))))
