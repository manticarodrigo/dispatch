(ns ui.lib.google.maps.autocomplete
  (:require [cljs-bean.core :refer (->clj)]))

(defonce ^:private !autocomplete-service (atom nil))

(defn- create-autocomplete-service []
  (js/google.maps.places.AutocompleteService.))

(defn search-places [text]
  (js/Promise.
   (fn [resolve _]
     (let [^js service @!autocomplete-service]
       (.getPlacePredictions
        service
        #js{:input text}
        (fn [results status]
          (when (or (= status "OK")
                    (count results))
            (resolve (->clj results)))))))))

(defn init-autocomplete []
  (reset! !autocomplete-service (create-autocomplete-service)))
