(ns api.resolvers.seat
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.seat :as seat]))

(defn create-seat
  [_ args context _]
  (seat/create context (->clj args)))

(defn fetch-seats
  [_ _ context _]
  (seat/find-all context))

(defn fetch-seat
  [_ args context _]
  (seat/find-unique context (->clj args)))
