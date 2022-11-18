(ns ui.lib.google.maps.places
  (:require [ui.lib.google.maps.serializer :refer (parse-place)]))

(defonce ^:private !places-service (atom nil))

(defn create-places-service [gmap]
  (js/google.maps.places.PlacesService. gmap))

(defn find-place [place-id]
  (js/Promise.
   (fn [resolve reject]
     (let [^js service @!places-service]
       (.getDetails
        service
        (clj->js {:placeId place-id,
                  :fields ["geometry"]})
        (fn [place status]
          (if (= status "OK")
            (resolve (parse-place place))
            (reject status))))))))

(defn init-places [gmap]
  (reset! !places-service (create-places-service gmap)))
