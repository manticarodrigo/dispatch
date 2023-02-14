(ns api.models.location
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]
   [api.models.agent :as agent]))

(defn create [^js context {:keys [agentId deviceId position createdAt]}]
  (p/let [^js agent (agent/active-agent context {:agentId agentId :deviceId deviceId})
          params {:data {:position position
                         :currentFor {:connect {:id agentId}}
                         :agent {:connect {:id agentId}}}}]
    (when agent
      (prisma/create!
       (.. context -prisma -location)
       (if createdAt
         (assoc-in params [:data :createdAt] createdAt)
         params)))))
