(ns api.resolvers.plan
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.plan :as plan]))

(defn create-plan
  [_ args context _]
  (plan/create-plan context (->clj args)))

(defn fetch-organization-plans
  [_ _ context _]
  (plan/fetch-organization-plans context))

(defn fetch-organization-plan
  [_ args context _]
  (plan/fetch-organization-plan context (->clj args)))

(defn optimize-plan
  [_ args context _]
  (plan/optimize-plan context (->clj args)))
