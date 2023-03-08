(ns api.models.location
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]
   [api.models.user :as user]))

(defn create-location [^js context {:keys [position createdAt]}]
  (p/let [^js user (user/active-user context {:include {:agent true}})
          agent-id (.. user -agent -id)
          params {:data {:position position
                         :agent {:connect {:id agent-id}}}}]
    (when agent-id
      (prisma/create!
       (.. context -prisma -location)
       (if createdAt
         (assoc-in params [:data :createdAt] createdAt)
         params)))))
