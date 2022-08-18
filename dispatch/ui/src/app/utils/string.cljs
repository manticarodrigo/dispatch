(ns app.utils.string
  (:require [clojure.string :as s]))

(defn class-names [& classes]
  (->> classes
       (remove empty?)
       (map s/trim)
       (s/join " ")))
