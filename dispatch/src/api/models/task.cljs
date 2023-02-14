(ns api.models.task
  (:require
   [promesa.core :as p]
   [goog.object :as gobj]
   [api.util.prisma :as prisma]
   [api.filters.core :as filters]
   [api.models.user :refer (active-user)]
   [api.models.agent :refer (active-agent)]))

(defn create [^js context {:keys [agentId startAt placeIds route]}]
  (p/let [^js user (active-user context)]
    (prisma/create!
     (.. context -prisma -task)
     {:data {:user {:connect {:id (.-id user)}}
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
  (if agentId
    (p/-> (active-agent
           context
           {:agentId agentId
            :deviceId deviceId
            :query {:include
                    {:user
                     {:include
                      {:tasks
                       {:where (update-in (filters/task filters) [:AND] conj
                                          {:agent {:id agentId}})
                        :orderBy {:startAt "asc"}
                        :include {:agent true
                                  :stops {:include {:place true}
                                          :orderBy {:order "asc"}}}}}}}}})
          (gobj/get "user")
          (gobj/get "tasks"))
    (p/-> (active-user
           context
           {:include
            {:tasks
             {:where (filters/task filters)
              :orderBy {:startAt "asc"}
              :include {:agent true
                        :stops {:include {:place true}
                                :orderBy {:order "asc"}}}}}})
          (gobj/get "tasks"))))

(defn find-unique [^js context {:keys [taskId agentId deviceId]}]
  (if agentId
    (p/-> (active-agent
           context
           {:agentId agentId
            :deviceId deviceId
            :query {:include
                    {:tasks
                     {:where {:id taskId}
                      :include {:agent {:include {:location true}}
                                :stops {:include {:place true}
                                        :orderBy {:order "asc"}}}}}}})
          (gobj/get "tasks")
          first)
    (p/-> (active-user
           context
           {:include
            {:tasks
             {:where {:id taskId}
              :include {:agent {:include {:location true}}
                        :stops {:include {:place true}
                                :orderBy {:order "asc"}}}}}})
          (gobj/get "tasks")
          first)))

(defn find-by-place [^js context {:keys [agentId deviceId placeId filters]}]
  (if agentId
    (p/-> (active-agent
           context
           {:agentId agentId
            :deviceId deviceId
            :query {:include
                    {:user
                     {:include
                      {:tasks
                       {:where (update-in (filters/task filters) [:AND] conj
                                          {:stops {:some {:place {:id placeId}}}})
                        :orderBy {:startAt "asc"}
                        :include {:agent true
                                  :stops {:include {:place true}
                                          :orderBy {:order "asc"}}}}}}}}})
          (gobj/get "user")
          (gobj/get "tasks"))
    (p/-> (active-user
           context
           {:include
            {:tasks
             {:where (update-in (filters/task filters) [:AND] conj
                                {:stops {:some {:place {:id placeId}}}})
              :orderBy {:startAt "asc"}
              :include {:agent true
                        :stops {:include {:place true}
                                :orderBy {:order "asc"}}}}}})
          (gobj/get "tasks"))))
