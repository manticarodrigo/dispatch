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

(defn find-device [^js context {:keys [id token]}]
  (p/let [^js seat (prisma/find-unique-or-throw
                    (.. context -prisma -seat)
                    {:where {:id id}
                     :include {:device true}})
          device-token (some-> seat .-device .-token)]
    (cond
      (not device-token) (throw (anom/gql (anom/not-found :device-not-linked)))
      (not= device-token token) (throw (anom/gql (anom/incorrect :invalid-token)))
      :else seat)))

(defn active-seat
  [^js context {:keys [id token query] :or {query {}}}]
  (p/let [^js seat (prisma/find-first (.. context -prisma -seat)
                                      (merge {:where {:id id :device {:token token}}} query))]
    (or seat (find-device context {:id id :token token}))))

(defn find-all [^js context]
  (p/let [user (active-user context {:include
                                     {:seats
                                      {:orderBy {:location {:createdAt "desc"}}
                                       :include {:location true}}}})]
    (sort-by #(some-> % .-location .-createdAt) > (gobj/get user "seats"))))

(defn find-unique [^js context {:keys [id token filters]}]
  (if token
    (active-seat
     context
     {:id id
      :token token
      :query {:include {:device true
                        :location true
                        :routes {:where (filters/route filters)
                                 :orderBy {:startAt "asc"}
                                 :include {:stops {:include {:address true}}}}}}})
    (p/-> (active-user
           context
           {:include
            {:seats
             {:where {:id id}
              :include {:device true
                        :location true
                        :routes {:where (filters/route filters)
                                 :orderBy {:startAt "asc"}
                                 :include {:stops {:include {:address true}}}}}}}})
          (gobj/get "seats")
          first)))
