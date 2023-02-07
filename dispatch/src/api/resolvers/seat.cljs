(ns api.resolvers.seat
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.seat :as seat]))

(defn create
  [_ args context _]
  (seat/create context (->clj args)))

(defn find-all
  [_ _ context _]
  (seat/find-all context))

(defn find-unique
  [_ args context _]
  (seat/find-unique context (->clj args)))
