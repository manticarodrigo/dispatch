(ns api.models.seat
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create [^js context {:keys [user-id name]}]
  (p/let [^js seat (prisma/create!
                    (.. context -prisma -seat)
                    {:data {:name name
                            :user user-id}
                     :include {:sessions true}})]
    (some-> seat .-id)))
