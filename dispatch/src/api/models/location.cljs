(ns api.models.location
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create [^js context {:keys [seatId lat lng createdAt]}]
  (p/let [params {:data {:lat lat
                         :lng lng
                         :currentFor {:connect {:id seatId}}
                         :seat {:connect {:id seatId}}}}
          ^js location (prisma/create!
                        (.. context -prisma -location)
                        (if createdAt
                          (assoc-in params [:data :createdAt] createdAt)
                          params))]
    (some-> location .-id)))
