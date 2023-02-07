(ns tests.waypoint
  (:require
   [shadow.resource :refer (inline)]
   [promesa.core :as p]
   [tests.util.api :refer (send)]))

(defn create-arrival [waypoint-id]
  (p/let [query (inline "mutations/waypoint/create-arrival.graphql")
          variables {:waypointId waypoint-id}
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))
