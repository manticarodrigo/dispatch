(ns ui.utils.validation)

(defn latitude? [lat]
  (and (js/isFinite lat)
       (<= -90 lat 90)))

(defn longitude? [lng]
  (and (js/isFinite lng)
       (<= -180 lng 180)))
