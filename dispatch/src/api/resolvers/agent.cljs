(ns api.resolvers.agent
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.agent :as agent]))

(defn create
  [_ args context _]
  (agent/create context (->clj args)))

(defn find-all
  [_ _ context _]
  (agent/find-all context))

(defn find-unique
  [_ args context _]
  (agent/find-unique context (->clj args)))
