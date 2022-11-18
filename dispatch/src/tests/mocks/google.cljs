(ns tests.mocks.google
  (:require [cljs-bean.core :refer (->js)]))

(defn mock-autocomplete [data]
  (let [^js AutocompleteService (fn [])]
    (set!
     (.. AutocompleteService -prototype -getQueryPredictions)
     (fn [_ cb]
       (js/setTimeout
        (fn [] (cb (->js data) "OK"))
        500)))

    AutocompleteService))

(defn mock-places [data]
  (let [^js PlacesService (fn [])]
    (set!
     (.. PlacesService -prototype -getDetails)
     (fn [_ cb]
       (js/setTimeout
        (fn [] (cb (->js data) "OK"))
        500)))

    PlacesService))

(defn mock-google [data]
  (->js
   {:maps
    {:places
     {:PlacesService (mock-places data)
      :AutocompleteService (mock-autocomplete data)}}}))
