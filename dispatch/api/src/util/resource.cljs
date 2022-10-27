(ns util.resource
  (:require [nbb.core :as nbb]))

(defn slurp [path]
  (nbb/slurp (str "../ui/src/resources/" path)))
