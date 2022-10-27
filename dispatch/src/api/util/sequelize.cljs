(ns api.util.sequelize
  (:require [cljs-bean.core :refer (->js)]))

(defn append
  "takes sequelize instance, model name, column name, value, and where clause"
  [^js s m c v w]
  (.update m
           (->js (assoc {} (keyword c) (.fn s "array_append" (.col s c) v)))
           (->js {:where w})))
