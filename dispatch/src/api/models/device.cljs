(ns api.models.device
  (:require [api.util.prisma :as prisma]))

(defn create [^js context {:keys [agentId deviceId info]}]
  (prisma/create!
   (.. context -prisma -device)
   {:data {:id deviceId
           :info info
           :agent {:connect {:id agentId}}}}))
