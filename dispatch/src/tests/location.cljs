(ns tests.location
  (:require
   [shadow.resource :refer (inline)]
   [promesa.core :as p]
   [tests.util.api :refer (send)]
   [tests.util.location :refer (nearby)]))

(defn create [seat-id device-id created-at]
  (p/let [query (inline "mutations/location/create.graphql")
          [lat lng] (nearby)
          variables {:seatId seat-id
                     :deviceId device-id
                     :position {:latitude lat
                                :longitude lng
                                :heading (rand-int 360)}
                     :createdAt created-at}
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))
