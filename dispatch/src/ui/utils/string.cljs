(ns ui.utils.string
  (:require [clojure.string :as s]))

(defn class-names [& classes]
  (->> classes
       (remove empty?)
       (map s/trim)
       (s/join " ")))

(defn filter-text [text get-text-fn coll]
  (if (empty? text)
    coll
    (filter
     #(s/includes?
       (-> % get-text-fn s/lower-case)
       (s/lower-case text))
     coll)))
