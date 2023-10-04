(ns common.utils.number)

(defn scale-amount [amount factor]
  (let [scaled (-> amount js/parseFloat (* factor))]
    (if (> factor 1) (js/parseInt scaled) scaled)))
