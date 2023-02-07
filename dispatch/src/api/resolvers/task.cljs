(ns api.resolvers.task
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.task :as task]))

(defn create
  [_ args context _]
  (task/create context (->clj args)))

(defn find-all
  [_ args context _]
  (task/find-all context (->clj args)))

(defn find-unique
  [_ args context _]
  (task/find-unique context (->clj args)))

(defn find-by-place
  [parent args context _]
  (task/find-by-place context (merge {:placeId (.-id parent)} (->clj args))))
