(ns api.models.agent
  (:require [promesa.core :as p]
            [api.config :as config]
            [api.lib.notification :as notification]
            [api.util.prisma :as prisma]
            [api.util.anom :as anom]
            [api.filters.core :as filters]
            [api.models.user :refer (active-user)]))

(defn create [^js context {:keys [name phone]}]
  (p/let [^js user (active-user context {:include {:organization true}})
          ^js agent (prisma/create!
                     (.. context -prisma -agent)
                     {:data {:name name
                             :phone phone
                             :organization {:connect {:id (.. user -organization -id)}}}})]
    (notification/send-sms phone (str "Login to your account at " config/SITE_URL "/agent/" (.. agent -id)))
    agent))

(defn find-device [^js context {:keys [agentId deviceId]}]
  (p/let [^js agent (prisma/find-unique
                     (.. context -prisma -agent)
                     {:where {:id agentId}
                      :include {:device true}})
          ^js device (prisma/find-unique
                      (.. context -prisma -device)
                      {:where {:id deviceId}
                       :include {:agent true}})
          agent-device-id (some-> agent .-device .-id)
          id-mismatch? (not= agent-device-id deviceId)]
    (cond
      (not agent) (throw (anom/gql (anom/not-found :agent-not-found)))
      (and device id-mismatch?) (throw (anom/gql (anom/incorrect :device-already-linked)))
      (not agent-device-id) (throw (anom/gql (anom/not-found :device-not-linked)))
      id-mismatch? (throw (anom/gql (anom/incorrect :device-token-invalid)))
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
    (first (.. result -organization -agents))))
