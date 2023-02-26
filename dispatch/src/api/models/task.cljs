(ns api.models.task
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]
   [api.filters.core :as filters]
   [api.models.user :as user]))

(defn create-task [^js context {:keys [agentId startAt placeIds route]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})]
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

(def tasks-include
  {:agent true
   :stops {:orderBy {:order "asc"}
           :include
           {:place true}}})

(defn tasks-query [filters]
  {:tasks
   {:where (filters/task filters)
    :orderBy {:startAt "asc"}
    :include tasks-include}})

(defn task-query [taskId]
  {:tasks
   {:where {:id taskId}
    :include tasks-include}})

(defn fetch-organization-tasks [^js context {:keys [filters]}]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include (tasks-query filters)}}})]
    (.. user -organization -tasks)))

(defn fetch-agent-tasks [^js context {:keys [filters]}]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include (tasks-query filters)}}})]
    (.. user -agent -tasks)))

(defn fetch-organization-task [^js context {:keys [taskId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include
                                                (task-query taskId)}}})]
    (first (.. user -organization -tasks))))

(defn fetch-agent-task [^js context {:keys [taskId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                (task-query taskId)}}})]
    (first (.. user -agent -tasks))))
