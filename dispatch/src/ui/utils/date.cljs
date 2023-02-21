(ns ui.utils.date
  (:refer-clojure :exclude [set])
  (:require [date-fns :as d]
            ["date-fns/locale" :refer (en es)]
            [ui.subs :refer (listen)]))

(def dict {:en en :es es})

(defn localize [f]
  (fn [& args]
    (let [language (listen [:language])
          locale ((keyword language) dict)
          options #js{:locale locale :addSuffix true}]
      (apply f (conj (vec args) options)))))

(def format (localize d/format))
(def formatRelative (localize d/formatRelative))
(def formatDistanceToNowStrict (localize d/formatDistanceToNowStrict))

(def set d/set)
(def getDay d/getDay)
(def getDate d/getDate)
(def isToday d/isToday)
(def isBefore d/isBefore)
(def isAfter d/isAfter)
(def startOfDay d/startOfDay)
(def endOfDay d/endOfDay)
(def subHours d/subHours)
(def addSeconds d/addSeconds)
