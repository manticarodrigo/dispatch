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

(defn find-all [^js context {:keys [filters]}]
  (p/-> (active-user
         context
         {:include
          {:tasks
           {:where (filters/task filters)
            :orderBy {:startAt "asc"}
            :include {:seat true
                      :waypoints {:include {:place true}
                                  :orderBy {:order "asc"}}}}}})
        (gobj/get "tasks")))

(defn find-unique [^js context {:keys [taskId seatId deviceId]}]
  (if deviceId
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

(defn find-by-place [^js context {:keys [placeId filters]}]
  (let [{:keys [start end status]} filters]
    (p/-> (active-user
           context
           ;; TODO: move to place model
           {:include
            {:tasks
             {:where {:AND [{:waypoints {:some {:place {:id placeId}}}}
                            {:waypoints (condp = status
                                          "INCOMPLETE" {:some {:arrivedAt {:equals nil}}}
                                          "COMPLETE" {:every {:arrivedAt {:not nil}}}
                                          nil {})}
                            (if (and start end) {:startAt {:gte start}} {})
                            (if (and start end) {:startAt {:lte end}} {})]}
              :orderBy {:startAt "asc"}
              :include {:seat true
                        :waypoints {:include {:place true}
                                    :orderBy {:order "asc"}}}}}})
          (gobj/get "tasks"))))
