(ns api.resolvers.agent
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.agent :as agent]))

(defn create-agent
  [_ args context _]
  (agent/create-agent context (->clj args)))

(defn fetch-organization-agents
  [_ _ context _]
  (agent/fetch-organization-agents context))

(defn fetch-organization-agent
  [_ args context _]
  (agent/fetch-organization-agent context (->clj args)))

(defn fetch-organization-performance
  [_ args context _]
  (agent/fetch-organization-performance context (->clj args)))
