(ns app.hooks.use-location
  (:require
   [react :refer (useEffect)]
   [re-frame.core :as rf]
   ["@capacitor/geolocation" :refer (Geolocation)]
   [clojure.core :refer [atom]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [app.utils.logger :as logger]))

(defonce ^:private position-options
  (clj->js {:enableHighAccuracy true
            :timeout 10000
            :maximumAge 0}))

(defonce ^:private !location-watch-id (atom nil))

(defn- parse-position [pos]
  (let [coords (.-coords pos)
        lat (.-latitude coords)
        lng (.-longitude coords)]
    {:lat lat :lng lng}))

(defn- get-position []
  (-> (.getCurrentPosition
       Geolocation
       position-options)
      (.then #(rf/dispatch [:origin/set (parse-position %)]))
      (.catch logger/error)))

(defn- watch-position []
  (go
    (reset!
     !location-watch-id
     (<p!
      (.watchPosition
       Geolocation
       position-options
       (fn [pos err]
         (if (some? pos)
           (rf/dispatch [:location/set (parse-position pos)])
           (logger/error err))))))))

(defn- clear-watch [id]
  (.clearWatch Geolocation #js{:id id}))

(defn use-location []
  (useEffect
   (fn []
     (fn []
       (when (some? @!location-watch-id)
         (clear-watch @!location-watch-id)
         (reset! !location-watch-id nil))))
   #js[])
  {:get get-position :watch watch-position})
