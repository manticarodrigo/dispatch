(ns api.models.agent
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.util.anom :as anom]
            [api.filters.core :as filters]
            [api.models.user :refer (active-user)]))

(defn create [^js context {:keys [name]}]
  (p/let [^js user (active-user context {:include {:organization true}})]
    (prisma/create!
     (.. context -prisma -agent)
     {:data {:name name
             :organization {:connect {:id (.. user -organization -id)}}}})))

(defn find-device [^js context {:keys [agentId deviceId]}]
  (p/let [^js agent (prisma/find-unique-or-throw
                     (.. context -prisma -agent)
                     {:where {:id agentId}
                      :include {:device true}})
          device-id (some-> agent .-device .-id)]
    (cond
      (not device-id) (throw (anom/gql (anom/not-found :device-not-linked)))
      (not= device-id deviceId) (throw (anom/gql (anom/incorrect :invalid-token)))
      :else agent)))

(defn active-agent
  [^js context {:keys [agentId deviceId query] :or {query {}}}]
  (p/let [^js agent (prisma/find-first (.. context -prisma -agent)
                                       (merge {:where {:id agentId :device {:id deviceId}}} query))]
    (or agent (find-device context {:agentId agentId :deviceId deviceId}))))

(defn find-all [^js context]
  (p/let [^js user (active-user
                    context
                    {:include
                     {:organization
                      {:include
                       {:agents
                        {:orderBy {:location {:createdAt "desc"}}
                         :include
                         {:device true
                          :location true}}}}}})
          ^js agents (.. user -organization -agents)]
    ;; this moves null values to the end of the list
    (sort-by #(some-> % .-location .-createdAt) > agents)))

(defn find-unique [^js context {:keys [agentId filters]}]
  (p/let [^js result (active-user
                      context
                      {:include
                       {:organization
                        {:include
                         {:agents
                          {:where {:id agentId}
                           :include {:device true
                                     :location true
                                     :tasks {:where (filters/task filters)
                                             :orderBy {:startAt "asc"}
                                             :include {:stops {:include {:place true}}}}}}}}}})]
    (some-> result .-organization .-agents first)))
