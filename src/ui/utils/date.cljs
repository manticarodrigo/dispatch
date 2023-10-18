(ns ui.utils.date
  (:refer-clojure :exclude [set])
  (:require [date-fns :as d]
            ["date-fns/locale" :refer (en es)]
            [ui.subs :refer (listen)]))

(def dict {:en en :es es})

(defn localize-and-suffix [f]
  (fn [& args]
    (let [language (listen [:language])
          locale ((keyword language) dict)
          options #js{:locale locale :addSuffix true}]
      (apply f (conj (vec args) options)))))

(defn localize [f]
  (fn [& args]
    (let [language (listen [:language])
          locale ((keyword language) dict)
          options #js{:locale locale}]
      (apply f (conj (vec args) options)))))

(def format (localize-and-suffix d/format))
(def formatRelative (localize-and-suffix d/formatRelative))
(def formatDistanceToNowStrict (localize-and-suffix d/formatDistanceToNowStrict))
(def formatDistanceStrict (localize d/formatDistanceStrict))

(def set d/set)
(def getDay d/getDay)
(def getDate d/getDate)
(def isToday d/isToday)
(def isBefore d/isBefore)
(def isAfter d/isAfter)
(def isSameDay d/isSameDay)
(def startOfDay d/startOfDay)
(def endOfDay d/endOfDay)
(def subHours d/subHours)
(def subDays d/subDays)
(def addSeconds d/addSeconds)
(def addMinutes d/addMinutes)
(def addHours d/addHours)
(def addDays d/addDays)
