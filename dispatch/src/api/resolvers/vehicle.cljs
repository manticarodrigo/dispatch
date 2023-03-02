(ns api.resolvers.vehicle
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.vehicle :as vehicle]))

(defn create-vehicle
  [_ args context _]
  (vehicle/create-vehicle context (->clj args)))

(defn fetch-organization-vehicles
  [_ _ context _]
  (vehicle/fetch-organization-vehicles context))
