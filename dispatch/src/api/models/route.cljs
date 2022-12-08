(ns api.models.route
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create [^js context {:keys [seatId startAt addressIds]}]
  (p/let [^js route (prisma/create!
                     (.. context -prisma -route)
                     {:data {:user {:connect {:id (.. context -user -id)}}
                             :seat {:connect {:id seatId}}
                             :stops {:create (mapv (fn [id]
                                                     {:address {:connect {:id id}}})
                                                   addressIds)}
                             :startAt startAt}})]
    (some-> route .-id)))

(defn find-all [^js context {:keys [filters]}]
  (let [{:keys [start end]} filters
        and (if (and start end)
              [{:startAt {:gte start}}
               {:startAt {:lte end}}]
              [])]
    (prisma/find-many
     (.. context -prisma -route)
     {:where {:user {:id (.. context -user -id)}
              :AND and}
      :orderBy {:startAt "asc"}
      :include {:seat true
                :stops {:include {:address true}}}})))

(defn find-unique [^js context {:keys [id]}]
  (prisma/find-unique
   (.. context -prisma -route)
   {:where {:id id}
    :include {:seat {:include {:location true}}
              :stops {:include {:address true}}}}))
