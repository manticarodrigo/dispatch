(ns tests.shipment
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]))

(defn create-shipment [variables]
  (p/let [query (inline "mutations/shipment/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn fetch-organization-shipments []
  (p/let [query (inline "queries/user/organization/fetch-shipments.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))
