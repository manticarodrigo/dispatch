(ns tests.stop
  (:require
   [shadow.resource :refer (inline)]
   [promesa.core :as p]
   [tests.util.api :refer (send)]))

(defn create-arrival [stop-id]
  (p/let [query (inline "mutations/stop/create-arrival.graphql")
          variables {:stopId stop-id}
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))
