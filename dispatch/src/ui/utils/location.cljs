(ns ui.utils.location)

(defn position-to-lat-lng [{:keys [latitude longitude]}]
  {:lat latitude :lng longitude})
