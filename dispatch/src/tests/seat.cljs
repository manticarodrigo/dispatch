(ns tests.seat
  (:require ["@faker-js/faker" :refer (faker)]
            [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]))

(defn create-seat []
  (p/let [query (inline "mutations/seat/create.graphql")
          variables {:name (.. faker -name fullName)}
          request  {:query query :variables variables}
          result (send request)]
    {:query query
     :variables variables
     :request request
     :result result}))

(defn find-seats []
  (p/let [query (inline "queries/seat/fetch-all.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))
