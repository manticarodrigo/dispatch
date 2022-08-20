(ns app.utils.google.maps.autocomplete)

(set! *warn-on-infer* false)

(defonce ^:private !autocomplete-service (atom nil))

(defn- create-autocomplete-service []
  (js/google.maps.places.AutocompleteService.))

(defn search-places [text]
  (js/Promise.
   (fn [resolve _]
     (let [service @!autocomplete-service]
       (.getQueryPredictions
        service
        #js{:input text}
        (fn [results status]
          (when (or (= status js/google.maps.places.PlacesServiceStatus.OK)
                    (count results))
            (resolve results))))))))

(defn init-autocomplete []
  (reset! !autocomplete-service (create-autocomplete-service)))
