(ns api.resolvers.seat
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.seat :as model]))

(defn create-seat
  [_ args context _]
  (model/create context (->clj args)))

(defn fetch-seats
  [_ _ context _]
  (model/find-all context))

(defn fetch-seat
  [_ args context _]
  (model/find-unique context (->clj args)))
