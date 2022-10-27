(ns app.utils.google.maps.marker)

(defonce ^:private !markers (atom []))

(defn create-marker! [options]
  (let [marker (js/google.maps.Marker. (clj->js options))]
    (swap! !markers conj marker)
    marker))

(defn clear-markers! []
  (doseq [^js marker @!markers] (.setMap marker nil))
  (reset! !markers []))
