(ns ui.utils.error
  (:require [ui.utils.i18n :refer (tr)]))

(defn tr-error [e]
  (let [{:keys [reason errors]} e]
    (or (tr [(keyword (str "error/" reason))])
        (tr [:error/unknown]))))
