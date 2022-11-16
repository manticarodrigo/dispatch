(ns tests.address
  (:require
   ["@faker-js/faker" :refer (faker)]
   [shadow.resource :refer (inline)]
   [promesa.core :as p]
   [tests.util.api :refer (send)]))

(defn create-address []
  (p/let [query (inline "mutations/address/create.graphql")
          variables {:name (.. faker -company name)
                     :description (.. faker -address (streetAddress true))
                     :lat (js/parseFloat (.. faker -address latitude))
                     :lng (js/parseFloat (.. faker -address longitude))}
          request  {:query query :variables variables}
          result (send request)]
    {:query query
     :variables variables
     :request request
     :result result}))

(defn fetch-addresses []
  (p/let [query (inline "queries/address/fetch-all.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))
