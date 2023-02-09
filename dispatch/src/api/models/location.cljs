(ns api.models.location
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]
   [api.models.seat :as seat]))

(defn create [^js context {:keys [seatId deviceId position createdAt]}]
  (p/let [^js seat (seat/active-seat context {:seatId seatId :deviceId deviceId})
          params {:data {:position position
                         :currentFor {:connect {:id seatId}}
                         :seat {:connect {:id seatId}}}}]
    (when seat
      (prisma/create!
       (.. context -prisma -location)
       (if createdAt
         (assoc-in params [:data :createdAt] createdAt)
         params)))))
