(ns api.resolvers.route
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.route :as route]))

(defn create-route
  [_ args context _]
  (route/create context (->clj args)))

(defn fetch-route
  [_ args context _]
  (route/find-unique context (->clj args)))

(defn fetch-routes
  [_ args context _]
  (route/find-all context (->clj args)))

(defn fetch-routes-by-address
  [parent args context _]
  (route/find-by-address context (merge (->clj parent) (->clj args))))
