(ns tests.user
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]))

(defn logged-in-user []
  (p/let [query (inline "queries/user/fetch.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))
