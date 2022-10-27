(ns app.utils.google.maps.autocomplete)

(defonce ^:private !autocomplete-service (atom nil))

(defn- create-autocomplete-service []
  (js/google.maps.places.AutocompleteService.))

(defn search-places [text]
  (js/Promise.
   (fn [resolve _]
     (let [^js service @!autocomplete-service]
       (.getQueryPredictions
        service
        #js{:input text}
        (fn [results status]
          (when (or (= status js/google.maps.places.PlacesServiceStatus.OK)
                    (count results))
            (resolve (js->clj results :keywordize-keys true)))))))))

(defn init-autocomplete []
  (reset! !autocomplete-service (create-autocomplete-service)))
