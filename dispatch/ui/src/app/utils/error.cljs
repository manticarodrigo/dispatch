(ns app.utils.error
  (:require [app.utils.i18n :refer (tr)]))

(defn tr-error [e]
  (or (tr [(keyword (str "error/" (:reason e)))])
      (tr [:error/unknown])))
