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
             :startAt startAt
             :versions
             {:create
              [{:stops {:create
                        (mapv (fn [[idx id]]
                                {:order idx
                                 :place {:connect {:id id}}})
                              (map-indexed vector placeIds))}
                :route route}]}}
      :include {:agent true}})))

(def tasks-include
  {:agent true
   :versions {:orderBy {:createdAt "desc"} 
              :take 1
              :include {:stops 
                        {:orderBy {:order "asc"}
                         :include {:place true}}}}})

; (def tasks-include
;   {
;    :agent true
;    :stops true
;   })

(defn tasks-query [filters]
  (prn filters)
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
                                                (task-query taskId)}}})
          ^js tasks (.. user -organization -tasks)] 
    (.forEach 
     tasks
      (fn [^js task]
        (set! (.-stops task) (.-versions task))))
    (prn (first tasks))
    (first tasks)))

(defn fetch-agent-task [^js context {:keys [taskId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                (task-query taskId)}}})]
    (first (.. user -agent -tasks))))