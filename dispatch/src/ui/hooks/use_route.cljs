(ns ui.hooks.use-route
  (:require
   [react :refer (createContext useContext)]
   [cljs-bean.core :refer (->clj)]
   [ui.hooks.use-map :refer (use-map)]
   [ui.hooks.use-location :refer (use-location)]))

(defonce ^:private route-context (createContext {}))

(defn use-route-context []
  (let [val (useContext route-context)]
    (->clj val)))

(def route-context-provider (.-Provider route-context))

(defn use-route []
  (let [map-props (use-map)
        location-props (use-location)]
    (conj map-props location-props)))
