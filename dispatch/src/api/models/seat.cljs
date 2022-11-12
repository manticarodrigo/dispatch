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
