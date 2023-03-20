(ns common.utils.date
  (:require ["date-fns" :as d]))

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

(def date-scalar-map
  {:serialize #(.getTime %)
   :parseValue #(js/Date. %)
   :parseLiteral #(when (= "IntValue" (.. % -kind))
                    (js/Date. (.. % -value)))})

(defn parse-date [date]
  (if date (-> date js/parseInt js/Date.) (js/Date.)))

(defn military->minutes [military-time]
  (let [time (if (= (count military-time) 3) (str "0" military-time) military-time)
        hours (-> time
                  (subs 0 2)
                  (js/parseInt))
        minutes (-> time
                    (subs 2)
                    (js/parseInt))]
    (+ (* hours 60) minutes)))

(defn military->date [military-time]
  (-> (js/Date.)
      (d/startOfDay)
      (d/addDays 1)
      (d/addMinutes (military->minutes military-time))))

(defn military->window [start end]
  (when (and (not-empty start) (not-empty end))
    {:startAt (-> start military->date)
     :endAt (-> end military->date)}))
