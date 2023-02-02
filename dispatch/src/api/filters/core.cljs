(ns api.filters.core)

(defn route [{:keys [start end status]}]
  {:stops (condp = status
            "INCOMPLETE" {:some {:arrivedAt {:equals nil}}}
            "COMPLETE" {:every {:arrivedAt {:not nil}}}
            nil {})
   :AND (if (and start end)
          [{:startAt {:gte start}}
           {:startAt {:lte end}}]
          [])})
