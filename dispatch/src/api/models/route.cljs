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

(defn find-all [^js context]
  (prisma/find-many
   (.. context -prisma -route)
   {:where {:user {:id (.. context -user -id)}}
    :include {:seat true
              :stops {:include {:address true}}}}))
