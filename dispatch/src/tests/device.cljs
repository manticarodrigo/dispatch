(ns tests.device
  (:require
   [shadow.resource :refer (inline)]
   [promesa.core :as p]
   [tests.util.api :refer (send)]))

(defn create [variables]
  (p/let [query (inline "mutations/device/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))
