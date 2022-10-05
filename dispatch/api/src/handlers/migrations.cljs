(ns handlers.migrations
  (:require [promesa.core :as p]
            [resolvers.migrations :refer (up down)]))

(defn migrate-up
  [{:keys [ctx]}]
  (fn [_ res]
    (p/let [migration-res (up ctx)]
      (.send res migration-res))))

(defn migrate-down
  [{:keys [ctx]}]
  (fn [_ res]
    (p/let [migration-res (down ctx)]
      (.send res migration-res))))
