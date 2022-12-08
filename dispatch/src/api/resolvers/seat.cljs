(ns api.resolvers.seat
  (:require [promesa.core :as p]
            [cljs-bean.core :refer (->clj)]
            [api.util.anom :as anom]
            [api.models.seat :as models.seat]))

(defn create-seat
  [_ args context _]
  (-> (models.seat/create context (->clj args))
      (p/catch anom/handle-resolver-error)))

(defn fetch-seats
  [_ _ context _]
  (-> (models.seat/find-all context)
      (p/catch anom/handle-resolver-error)))

(defn fetch-seat
  [_ args context _]
  (-> (models.seat/find-unique context (->clj args))
      (p/catch anom/handle-resolver-error)))
