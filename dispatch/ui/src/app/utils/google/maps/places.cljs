(ns app.utils.google.maps.places
  (:require [app.utils.google.maps.serializer :refer (parse-place)]))

(defonce ^:private !places-service (atom nil))

(defn find-place [place-id]
  (js/Promise.
   (fn [resolve reject]
     (let [^js service @!places-service]
       (.getDetails
        service
        (clj->js {:placeId place-id,
                  :fields ["geometry"]})
        (fn [place status]
          (if (= status js/google.maps.places.PlacesServiceStatus.OK)
            (resolve (parse-place place))
            (reject status))))))))

(defn init-places [gmap]
  (js/google.maps.places.PlacesService. gmap))

