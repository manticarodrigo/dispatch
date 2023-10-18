(ns api.models.agent
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.filters.core :as filters]
            [api.models.user :refer (active-user)]))

(defn create-agent [^js context {:keys [name phone]}]
  (p/let [^js user (active-user context {:include {:organization true}})
          ^js agent-user (prisma/create!
                          (.. context -prisma -user)
                          {:data {:phone phone
                                  :agent
                                  {:create
                                   {:name name
                                    :organization
                                    {:connect {:id (.. user -organization -id)}}}}}
                           :include {:agent true}})]
    (.. agent-user -agent)))

(defn fetch-organization-agents [^js context]
  (p/let [^js user (active-user
                    context
                    {:include
                     {:organization
                      {:include
                       {:agents
                        {:include
                         {:user true
                          :locations
                          {:take 1
                           :orderBy {:createdAt "desc"}}}}}}}})
          ^js agents (.. user -organization -agents)]
    (.forEach
     agents
     (fn [^js agent]
       (set! (.-location agent) (first (.-locations agent)))))
    ;; this moves null values to the end of the list
    (sort-by #(some-> ^js % .-location .-createdAt) > agents)))

(defn fetch-organization-agent [^js context {:keys [agentId filters]}]
  (p/let [^js result (active-user
                      context
                      {:include
                       {:organization
                        {:include
                         {:agents
                          {:where {:id agentId}
                           :include
                           {:user true
                            :locations {:take 1
                                        :orderBy {:createdAt "desc"}}
                            :tasks {:where (filters/task filters)
                                    :orderBy {:startAt "asc"}
                                    :include {:stops {:include {:place true}}}}}}}}}})
          ^js agent (first (.. result -organization -agents))]
    (set! (.-location agent) (first (.. agent -locations)))
    agent))
