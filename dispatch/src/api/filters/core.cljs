(ns api.filters.core
  (:require [date-fns :as d]))

(defn session [id]
  {:sessions {:some {:id id
                     :createdAt {:gte (d/subDays (js/Date.) 7)}}}})

(defn task [{:keys [start end status]}]
  {:waypoints (condp = status
                "INCOMPLETE" {:some {:arrivedAt {:equals nil}}}
                "COMPLETE" {:every {:arrivedAt {:not nil}}}
                nil {})
   :AND (if (and start end)
          [{:startAt {:gte start}}
           {:startAt {:lte end}}]
          [])})
