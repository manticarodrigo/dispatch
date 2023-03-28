(ns api.models.task
  (:require [promesa.core :as p]
            [cljs-bean.core :refer (->clj)]
            [api.util.prisma :as prisma]
            [api.filters.core :as filters]
            [api.models.user :as user]))

(def task-include
  {:agent true
   :stops {:orderBy {:order "asc"}
           :include
           {:place true
            :shipment
            {:include
             {:windows true}}}}})

(defn tasks-query [filters]
  {:tasks
   {:where (filters/task filters)
    :orderBy {:startAt "asc"}
    :include task-include}})

(defn task-query [taskId]
  {:tasks
   {:where {:id taskId}
    :include task-include}})

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

;; (defn update-task [^js context {:keys [taskId stops route]}]
;;   (p/let [^js user (user/active-user context {:include
;;                                               {:organization
;;                                                {:include (task-query taskId)}}})]
;;     (prisma/update!
;;      (.. context -prisma -task)
;;      {:where {:id taskId}
;;       :data {:organization {:connect {:id (.. user -organization -id)}}
;;              :stops {:create (mapv (fn [[idx id]]
;;                                      {:order idx
;;                                       :place {:connect {:id id}}})
;;                                    (map-indexed vector stops))}
;;              :route route}
;;       :include tasks-include})))

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
  (when taskId
    (p/let [^js user (user/active-user context {:include
                                                {:organization
                                                 {:include
                                                  (task-query taskId)}}})]
      (first (.. user -organization -tasks)))))

(defn fetch-agent-task [^js context {:keys [taskId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                (task-query taskId)}}})]
    (first (.. user -agent -tasks))))

(defn optimize-task [^js context {:keys [taskId]}]
  (p/let [^js task (fetch-organization-task context {:taskId taskId})
          stops (->clj (.. task -stops))
          {complete true incomplete false} (group-by #(-> % :arrivedAt some?) stops)]
    (println complete)
    (println incomplete)
    ;; (prisma/update!
    ;;  (.. context -prisma -task)
    ;;  {:where {:id taskId}
    ;;   :data {:route (.. task -result -route)
    ;;          :stops {:create (mapv (fn [[idx id]]
    ;;                                  {:order idx
    ;;                                   :place {:connect {:id id}}})
    ;;                                (map-indexed vector (.. task -result -placeIds)))}}
    ;;   :include {:agent true
    ;;             :stops {:include {:place true}
    ;;                     :orderBy {:order "asc"}}}})
    ))
