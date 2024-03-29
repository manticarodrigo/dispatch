(ns api.filters.core
  (:require [date-fns :as d]))

(defn session [id]
  {:sessions {:some {:id id
                     :createdAt {:gte (d/subDays (js/Date.) 7)}}}})

(defn task [{:keys [start end status]}]
  {:AND (concat [{:stops
                  (case status
                    "INCOMPLETE" {:some {:finishedAt {:equals nil}}}
                    "COMPLETE" {:every {:finishedAt {:not nil}}}
                    {})}]
                (if (and start end)
                  [{:startAt {:gte start}}
                   {:startAt {:lte end}}]
                  []))})
