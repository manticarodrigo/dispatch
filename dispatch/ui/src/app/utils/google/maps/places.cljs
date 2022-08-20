(ns app.utils.google.maps.places)

(set! *warn-on-infer* false)

(defonce ^:private !places-service (atom nil))

(defn find-place [place-id]
  (js/Promise.
   (fn [resolve reject]
     (let [service @!places-service]
       (.getDetails
        service
        (clj->js {:placeId place-id,
                  :fields ["geometry"]})
        (fn [place status]
          (if (= status js/google.maps.places.PlacesServiceStatus.OK)
            (resolve place)
            (reject status))))))))


(defn init-places [gmap]
  (js/google.maps.places.PlacesService. gmap))

