(ns tests.mocks.google
  (:require [cljs-bean.core :refer (->js)]))

(defn mock-autocomplete [data]
  (let [^js AutocompleteService (fn [])]
    (set!
     (.. AutocompleteService -prototype -getPlacePredictions)
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

(defn mock-directions [data]
  (let [^js DirectionsService (fn [])]
    (set!
     (.. DirectionsService -prototype -route)
     (fn [_ cb]
       (js/setTimeout
        (fn [] (cb (->js data) "OK"))
        500)))

    DirectionsService))

(defn mock-lat-lng []
  (->js {:lat (fn []) :lng (fn [])}))

(defn mock-google [data]
  (->js
   {:maps
    {:DirectionsService (mock-directions data)
     :places
     {:PlacesService (mock-places data)
      :AutocompleteService (mock-autocomplete data)}}}))
