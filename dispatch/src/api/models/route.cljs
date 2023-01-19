(ns api.models.route
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create [^js context {:keys [seatId startAt addressIds route]}]
  (p/let [^js route (prisma/create!
                     (.. context -prisma -route)
                     {:data {:user {:connect {:id (.. context -user -id)}}
                             :seat {:connect {:id seatId}}
                             :stops {:create (mapv (fn [[idx id]]
                                                     {:order idx
                                                      :address {:connect {:id id}}})
                                                   (map-indexed vector addressIds))}
                             :route route
                             :startAt startAt}})]
    (some-> route .-id)))

(defn find-all [^js context {:keys [filters]}]
  (let [{:keys [start end status]} filters]
    (prisma/find-many
     (.. context -prisma -route)
     {:where {:user {:id (.. context -user -id)}
              :stops (condp = status
                       "INCOMPLETE" {:some {:arrivedAt {:equals nil}}}
                       "COMPLETE" {:every {:arrivedAt {:not nil}}}
                       nil {})
              :AND (if (and start end)
                     [{:startAt {:gte start}}
                      {:startAt {:lte end}}]
                     [])}
      :orderBy {:startAt "asc"}
      :include {:seat true
                :stops {:include {:address true}
                        :orderBy {:order "asc"}}}})))

(defn find-unique [^js context {:keys [id]}]
  (prisma/find-unique
   (.. context -prisma -route)
   {:where {:id id}
    :include {:seat {:include {:location true}}
              :stops {:include {:address true}
                      :orderBy {:order "asc"}}}}))

(defn find-by-address [^js context {:keys [id filters]}]
  (let [{:keys [start end status]} filters]
    (prisma/find-many
     (.. context -prisma -route)
     {:where {:AND [{:stops {:some {:address {:id id}}}}
                    {:stops (condp = status
                              "INCOMPLETE" {:some {:arrivedAt {:equals nil}}}
                              "COMPLETE" {:every {:arrivedAt {:not nil}}}
                              nil {})}
                    (if (and start end) {:startAt {:gte start}} {})
                    (if (and start end) {:startAt {:lte end}} {})]}
      :orderBy {:startAt "asc"}
      :include {:seat true
                :stops {:include {:address true}
                        :orderBy {:order "asc"}}}})))
