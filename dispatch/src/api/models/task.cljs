(ns api.models.task
  (:require
   [promesa.core :as p]
   [goog.object :as gobj]
   [api.util.prisma :as prisma]
   [api.filters.core :as filters]
   [api.models.user :refer (active-user)]
   [api.models.seat :refer (active-seat)]))

(defn create [^js context {:keys [seatId startAt placeIds route]}]
  (p/let [^js user (active-user context)]
    (prisma/create!
     (.. context -prisma -task)
     {:data {:user {:connect {:id (.-id user)}}
             :seat {:connect {:id seatId}}
             :waypoints {:create (mapv (fn [[idx id]]
                                         {:order idx
                                          :place {:connect {:id id}}})
                                       (map-indexed vector placeIds))}
             :route route
             :startAt startAt}
      :include {:seat true
                :waypoints {:include {:place true}
                            :orderBy {:order "asc"}}}})))

(defn find-all [^js context {:keys [seatId deviceId filters]}]
  (if seatId
    (p/-> (active-seat
           context
           {:seatId seatId
            :deviceId deviceId
            :query {:include
                    {:user
                     {:include
                      {:tasks
                       {:where (update-in (filters/task filters) [:AND] conj
                                          {:seat {:id seatId}})
                        :orderBy {:startAt "asc"}
                        :include {:seat true
                                  :waypoints {:include {:place true}
                                              :orderBy {:order "asc"}}}}}}}}})
          (gobj/get "user")
          (gobj/get "tasks"))
    (p/-> (active-user
           context
           {:include
            {:tasks
             {:where (filters/task filters)
              :orderBy {:startAt "asc"}
              :include {:seat true
                        :waypoints {:include {:place true}
                                    :orderBy {:order "asc"}}}}}})
          (gobj/get "tasks"))))

(defn find-unique [^js context {:keys [taskId seatId deviceId]}]
  (if seatId
    (p/-> (active-seat
           context
           {:seatId seatId
            :deviceId deviceId
            :query {:include
                    {:tasks
                     {:where {:id taskId}
                      :include {:seat {:include {:location true}}
                                :waypoints {:include {:place true}
                                            :orderBy {:order "asc"}}}}}}})
          (gobj/get "tasks")
          first)
    (p/-> (active-user
           context
           {:include
            {:tasks
             {:where {:id taskId}
              :include {:seat {:include {:location true}}
                        :waypoints {:include {:place true}
                                    :orderBy {:order "asc"}}}}}})
          (gobj/get "tasks")
          first)))

(defn find-by-place [^js context {:keys [seatId deviceId placeId filters]}]
  (if seatId
    (p/-> (active-seat
           context
           {:seatId seatId
            :deviceId deviceId
            :query {:include
                    {:user
                     {:include
                      {:tasks
                       {:where (update-in (filters/task filters) [:AND] conj
                                          {:waypoints {:some {:place {:id placeId}}}})
                        :orderBy {:startAt "asc"}
                        :include {:seat true
                                  :waypoints {:include {:place true}
                                              :orderBy {:order "asc"}}}}}}}}})
          (gobj/get "user")
          (gobj/get "tasks"))
    (p/-> (active-user
           context
           {:include
            {:tasks
             {:where (update-in (filters/task filters) [:AND] conj
                                {:waypoints {:some {:place {:id placeId}}}})
              :orderBy {:startAt "asc"}
              :include {:seat true
                        :waypoints {:include {:place true}
                                    :orderBy {:order "asc"}}}}}})
          (gobj/get "tasks"))))
