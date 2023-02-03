(ns api.models.seat
  (:require [promesa.core :as p]
            [goog.object :as gobj]
            [api.util.prisma :as prisma]
            [api.filters.core :as filters]
            [api.models.user :refer (logged-in-user)]))

(defn create [^js context {:keys [name]}]
  (p/let [^js user (logged-in-user context)]
    (prisma/create!
     (.. context -prisma -seat)
     {:data {:name name
             :user {:connect {:id (.-id user)}}}})))

(defn find-all [^js context]
  (p/let [user (logged-in-user context {:include
                                        {:seats
                                         {:orderBy {:location {:createdAt "desc"}}
                                          :include {:location true}}}})]
    (sort-by #(some-> % .-location .-createdAt) > (gobj/get user "seats"))))

(defn find-unique [^js context {:keys [id filters]}]
  (p/-> (logged-in-user
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
        first))
