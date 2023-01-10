(ns api.resolvers.route
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.route :as model]))

(defn create-route
  [_ args context _]
  (model/create context (->clj args)))

(defn fetch-route
  [_ args context _]
  (model/find-unique context (->clj args)))

(defn fetch-routes
  [_ args context _]
  (model/find-all context (->clj args)))

(defn fetch-routes-by-address
  [parent _ context _]
  (model/find-by-address context (->clj parent)))
