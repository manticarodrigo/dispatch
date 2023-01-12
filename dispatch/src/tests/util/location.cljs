(ns tests.util.location
  (:require ["@faker-js/faker" :refer (faker)]))

(def coordinate #js[12.072383 -86.245555])

(defn nearby []
  (let [coords (->
                faker
                .-address
                (.nearbyGPSCoordinate coordinate))]
    (mapv js/parseFloat coords)))

(defn generate-polyline [coordinates]
  (let [num-points 20
        !polyline (atom [])]
    (doseq [i (range (dec (count coordinates)))]
      (let [start-coord (nth coordinates i)
            end-coord (nth coordinates (inc i))
            delta-lat (/ (- (:lat end-coord) (:lat start-coord)) (+ num-points 1))
            delta-lng (/ (- (:lng end-coord) (:lng start-coord)) (+ num-points 1))]
        (doseq [j (range num-points)]
          (let [new-lat (+ (:lat start-coord) (* delta-lat (inc j)) (* (Math/random) 0.005))
                new-lng (+ (:lng start-coord) (* delta-lng (inc j)) (* (Math/random) 0.005))]
            (swap! !polyline conj {:lat new-lat :lng new-lng})))))
    (swap! !polyline conj (last coordinates))
    @!polyline))

