(ns app.utils.string
  (:require [clojure.string :as s]))

(defn class-names [& classes]
  (s/join
   " "
   classes))