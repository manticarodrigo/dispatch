(ns api.models.device
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create [^js context {:keys [seatId deviceId info]}]
  (p/let [^js device (prisma/create!
                      (.. context -prisma -device)
                      {:data {:id deviceId
                              :info info
                              :seat {:connect {:id seatId}}}})]
    (some-> device .-id)))
