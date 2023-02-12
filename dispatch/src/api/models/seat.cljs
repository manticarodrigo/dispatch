(ns api.models.seat
  (:require [promesa.core :as p]
            [goog.object :as gobj]
            [api.util.prisma :as prisma]
            [api.util.anom :as anom]
            [api.filters.core :as filters]
            [api.models.user :refer (active-user)]))

(defn create [^js context {:keys [name]}]
  (p/let [^js user (active-user context)]
    (prisma/create!
     (.. context -prisma -seat)
     {:data {:name name
             :user {:connect {:id (.-id user)}}}})))

(defn find-device [^js context {:keys [seatId deviceId]}]
  (p/let [^js seat (prisma/find-unique-or-throw
                    (.. context -prisma -seat)
                    {:where {:id seatId}
                     :include {:device true}})
          device-id (some-> seat .-device .-id)]
    (cond
      (not device-id) (throw (anom/gql (anom/not-found :device-not-linked)))
      (not= device-id deviceId) (throw (anom/gql (anom/incorrect :invalid-token)))
      :else seat)))

(defn active-seat
  [^js context {:keys [seatId deviceId query] :or {query {}}}]
  (p/let [^js seat (prisma/find-first (.. context -prisma -seat)
                                      (merge {:where {:id seatId :device {:id deviceId}}} query))]
    (or seat (find-device context {:seatId seatId :deviceId deviceId}))))

(defn find-all [^js context]
  (p/let [user (active-user context {:include
                                     {:seats
                                      {:orderBy {:location {:createdAt "desc"}}
                                       :include {:device true
                                                 :location true}}}})]
    (sort-by #(some-> % .-location .-createdAt) > (gobj/get user "seats"))))

(defn find-unique [^js context {:keys [seatId filters]}]
  (p/-> (active-user
         context
         {:include
          {:seats
           {:where {:id seatId}
            :include {:device true
                      :location true
                      :tasks {:where (filters/task filters)
                              :orderBy {:startAt "asc"}
                              :include {:waypoints {:include {:place true}}}}}}}})
        (gobj/get "seats")
        first))
