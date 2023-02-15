(ns common.utils.date
  ;; (:require [clojure.string :as s])
  )

(defn ten [i]
  (str (if (< i 10) "0" "") i))

(defn to-datetime-local [^js date]
  (let [YYYY (-> date .getFullYear)
        MM (-> date .getMonth inc ten)
        DD (-> date .getDate ten)
        HH (-> date .getHours ten)
        II (-> date .getMinutes ten)
        SS (-> date .getSeconds ten)]
    (str YYYY "-" MM "-" DD "T" HH ":" II ":" SS)))

;; (defn from-datetime-local [date]
;;   (let [full-iso (-> date .toISOString)
;;         [part-iso] (s/split full-iso ".")]
;;     (str part-iso "." "000Z")))

(def date-scalar-map
  {:serialize #(.getTime %)
   :parseValue #(js/Date. %)
   :parseLiteral #(when (= "IntValue" (.. % -kind))
                    (js/Date. (.. % -value)))})

(defn parse-date [date]
  (if date (-> date js/parseInt js/Date.) (js/Date.)))
