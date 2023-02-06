(ns api.filters.core
  (:require [date-fns :as d]))

(defn session [id]
  {:sessions {:some {:id id
                     :createdAt {:gte (d/subDays (js/Date.) 7)}}}})

(defn route [{:keys [start end status]}]
  {:stops (condp = status
            "INCOMPLETE" {:some {:arrivedAt {:equals nil}}}
            "COMPLETE" {:every {:arrivedAt {:not nil}}}
            nil {})
   :AND (if (and start end)
          [{:startAt {:gte start}}
           {:startAt {:lte end}}]
          [])})
