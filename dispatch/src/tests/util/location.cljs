(ns tests.util.location
  (:require ["@faker-js/faker" :refer (faker)]))

(def coordinate #js[12.072383 -86.245555])

(defn nearby []
  (let [coords (->
                faker
                .-address
                (.nearbyGPSCoordinate coordinate))]
    (mapv js/parseFloat coords)))
