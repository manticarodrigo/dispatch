(ns ui.lib.google.maps.serializer
  (:require [cljs-bean.core :refer (->clj)]))

(defn parse-lat-lng [^js lat-lng]
  (let [lat (.lat lat-lng)
        lng (.lng lat-lng)]
    {:lat lat :lng lng}))

(defn- parse-bounds [^js bounds]
  (let [{north :lat east :lng} (-> bounds .getNorthEast parse-lat-lng)
        {south :lat west :lng} (-> bounds .getSouthWest parse-lat-lng)]
    {:north north :east east :south south :west west}))

(defn- parse-path [path]
  (mapv parse-lat-lng path))

(defn- parse-leg [leg]
  (let [{:keys [distance
                duration
                end_address
                end_location]} (->clj leg)]
    {:distance (:value distance)
     :duration (:value duration)
     :address end_address
     :location (parse-lat-lng end_location)}))

(defn parse-route [^js response]
  (let [^js route (some-> response .-routes first)
        legs (mapv parse-leg (.-legs route))
        bounds (parse-bounds (.-bounds route))
        path (parse-path (.-overview_path route))]
    {:legs legs :bounds bounds :path path}))

(defn parse-place [^js place]
  (parse-lat-lng (-> place .-geometry .-location)))
