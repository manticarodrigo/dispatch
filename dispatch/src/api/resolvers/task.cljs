(ns api.resolvers.task
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.task :as task]))

(defn create-task
  [_ args context _]
  (task/create-task context (->clj args)))

(defn fetch-organization-tasks
  [_ args context _]
  (task/fetch-organization-tasks context (->clj args)))

(defn fetch-agent-tasks
  [_ args context _]
  (task/fetch-agent-tasks context (->clj args)))

(defn fetch-place-tasks
  [parent args context _]
  (task/fetch-place-tasks context (merge {:placeId (.-id parent)} (->clj args))))

(defn fetch-organization-task
  [_ args context _]
  (task/fetch-organization-task context (->clj args)))

(defn fetch-agent-task
  [_ args context _]
  (task/fetch-agent-task context (->clj args)))
