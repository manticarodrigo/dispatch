(ns tests.plan
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]))

(defn create-plan [variables]
  (p/let [query (inline "mutations/plan/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn fetch-organization-plans []
  (p/let [query (inline "queries/user/organization/fetch-plans.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))

(defn create-plan-tasks [variables]
  (p/let [query (inline "mutations/plan/create-plan-tasks.graphql")
          request {:query query :variables variables}
          result (send request)]
    {:query query
     :request request
     :result result}))