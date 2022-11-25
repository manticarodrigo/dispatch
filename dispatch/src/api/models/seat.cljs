(ns api.models.seat
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create [^js context {:keys [name]}]
  (p/let [user-id (.. context -user -id)
          ^js seat (prisma/create!
                    (.. context -prisma -seat)
                    {:data {:name name
                            :user {:connect {:id user-id}}}})]
    (some-> seat .-id)))

(defn find-all [^js context]
  (-> (prisma/find-many
       (.. context -prisma -seat)
       {:where {:user {:id (.. context -user -id)}}
        :orderBy {:location {:createdAt "desc"}}
        :include {:location true}})
      (.then (fn [res]
               (sort-by #(some-> % .-location .-createdAt) > res)))))

(defn find-unique [^js context {:keys [id]}]
  (prisma/find-unique
   (.. context -prisma -seat)
   {:where {:id id}
    :include {:location true
              :routes {:orderBy {:startAt "asc"}
                       :include {:stops {:include {:address true}}}}}}))
