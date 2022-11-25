(ns tests.util.location
  (:require ["@faker-js/faker" :refer (faker)]))

(def coordinate #js[12.072383 -86.245555])

(defn nearby []
  (mapv
   js/parseFloat
   (->
    faker
    .-address
    (.nearbyGPSCoordinate coordinate))))
