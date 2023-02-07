(ns api.resolvers.waypoint
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.waypoint :as waypoint]))

(defn create-arrival
  [_ args context _]
  (waypoint/create-arrival context (->clj args)))

(defn find-unique
  [_ args context _]
  (waypoint/find-unique context (->clj args)))
