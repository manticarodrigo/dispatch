(ns tests.vehicle
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]))

(defn create-vehicle [variables]
  (p/let [query (inline "mutations/vehicle/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn fetch-organization-vehicles []
  (p/let [query (inline "queries/user/organization/fetch-vehicles.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))
