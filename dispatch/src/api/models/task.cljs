(ns api.models.task
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]
   [api.filters.core :as filters]
   [api.models.user :refer (active-user)]
   [api.models.agent :refer (active-agent)]))

(defn create [^js context {:keys [agentId startAt placeIds route]}]
  (p/let [^js user (active-user context {:include {:organization true}})]
    (prisma/create!
     (.. context -prisma -task)
     {:data {:organization {:connect {:id (.. user -organization -id)}}
             :agent {:connect {:id agentId}}
             :stops {:create (mapv (fn [[idx id]]
                                     {:order idx
                                      :place {:connect {:id id}}})
                                   (map-indexed vector placeIds))}
             :route route
             :startAt startAt}
      :include {:agent true
                :stops {:include {:place true}
                        :orderBy {:order "asc"}}}})))

(defn find-all [^js context {:keys [agentId deviceId filters]}]
  (p/let [query {:include
                 {:organization
                  {:include
                   {:tasks
                    {:where (if agentId
                              (update-in (filters/task filters) [:AND] conj
                                         {:agent {:id agentId}})
                              (filters/task filters))
                     :orderBy {:startAt "asc"}
                     :include
                     {:agent true
                      :stops {:orderBy {:order "asc"}
                              :include
                              {:place true}}}}}}}}
          ^js result (if agentId
                       (active-agent context {:agentId agentId
                                              :deviceId deviceId
                                              :query query})
                       (active-user context query))]
    (.. result -organization -tasks)))

(defn find-unique [^js context {:keys [taskId agentId deviceId]}]
  (p/let [query {:include
                 {:organization
                  {:include
                   {:tasks
                    {:where {:id taskId}
                     :include
                     {:agent true
                      :stops {:orderBy {:order "asc"}
                              :include
                              {:place true}}}}}}}}
          ^js result (if agentId
                       (active-agent context {:agentId agentId
                                              :deviceId deviceId
                                              :query query})
                       (active-user context query))]
    (first (.. result -organization -tasks))))

(defn find-by-place [^js context {:keys [agentId deviceId placeId filters]}]
  (p/let [query {:include
                 {:organization
                  {:include
                   {:tasks
                    {:where (if agentId
                              (update-in (filters/task filters) [:AND] conj
                                         {:stops {:some {:place {:id placeId}}}})
                              (filters/task filters))
                     :orderBy {:startAt "asc"}
                     :include
                     {:agent true
                      :stops {:orderBy {:order "asc"}
                              :include
                              {:place true}}}}}}}}
          ^js result (if agentId
                       (active-agent context {:agentId agentId
                                              :deviceId deviceId
                                              :query query})
                       (active-user context query))]
    (.. result -organization -tasks)))
