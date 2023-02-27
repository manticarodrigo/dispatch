(ns api.resolvers.stop
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.stop :as stop]))

(defn create-arrival
  [_ args context _]
  (stop/create-arrival context (->clj args)))

(defn fetch-organization-stop
  [_ args context _]
  (stop/fetch-organization-stop context (->clj args)))

(defn fetch-agent-stop
  [_ args context _]
  (stop/fetch-agent-stop context (->clj args)))
