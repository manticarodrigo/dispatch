(ns api.models.address
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create [^js context {:keys [name lat lng]}]
  (p/let [user-id (.. context -user -id)
          ^js seat (prisma/create!
                    (.. context -prisma -address)
                    {:data {:name name
                            :lat lat
                            :lng lng
                            :user {:connect {:id user-id}}}})]
    (some-> seat .-id)))

(defn find-all [^js context]
  (prisma/find-many
   (.. context -prisma -address)
   {:where {:user {:id (.. context -user -id)}}}))
