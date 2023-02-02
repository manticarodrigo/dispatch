(ns api.resolvers.seat
  (:require [cljs-bean.core :refer (->clj)]
            [promesa.core :as p]
            [api.util.anom :as anom]
            [api.models.seat :as seat]))

(defn create-seat
  [_ args context _]
  (seat/create context (->clj args)))

(defn fetch-seats
  [_ _ context _]
  (seat/find-all context))

(defn fetch-seat
  [_ args context _]
  (p/let [{:keys [token] :as clj-args} (->clj args)
          ^js seat (seat/find-unique context clj-args)
          device-token (some-> seat .-device .-token)]
    (if token
      (cond
        (not device-token) (anom/gql (anom/not-found :device-not-linked))
        (not= device-token token) (anom/gql (anom/incorrect :invalid-token))
        :else seat)
      seat)))
