(ns app.utils.error
  (:require [app.utils.i18n :refer (tr)]))

(defn tr-error [e]
  (let [{:keys [reason errors]} e]
    (prn e)
    (or (tr [(keyword (str "error/" reason))])
        (tr [:error/unknown]))))
