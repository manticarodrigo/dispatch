(ns api.models.place
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :refer (active-user)]
            [api.models.agent :refer (active-agent)]))

(defn create [^js context {:keys [agentId deviceId name phone email description lat lng]}]
  (let [payload {:data {:name name
                        :phone phone
                        :email email
                        :description description
                        :lat lat
                        :lng lng}}]
    (if agentId
      (p/let [^js agent (active-agent
                         context
                         {:agentId agentId
                          :deviceId deviceId
                          :query {:include {:organization true}}})]
        (prisma/create!
         (.. context -prisma -place)
         (update-in payload [:data] merge
                    {:agent {:connect {:id agentId}}
                     :organization {:connect {:id (.. agent -organization -id)}}})))
      (p/let [^js user (active-user context {:include {:organization true}})]
        (prisma/create!
         (.. context -prisma -place)
         (update-in payload [:data] merge
                    {:organization {:connect {:id (.. user -organization -id)}}}))))))

(defn find-all [^js context {:keys [agentId deviceId]}]
  (p/let [query {:include
                 {:organization
                  {:include
                   {:places {:orderBy {:name "asc"}}}}}}
          ^js result (if agentId
                       (active-agent context {:agentId agentId
                                              :deviceId deviceId
                                              :query query})
                       (active-user context query))]
    (some-> result .-organization .-places)))

(defn find-unique [^js context {:keys [agentId deviceId placeId]}]
  (p/let [query {:include
                 {:organization
                  {:include
                   {:places {:where {:id placeId}}}}}}
          ^js result (if agentId
                       (active-agent context {:agentId agentId
                                              :deviceId deviceId
                                              :query query})
                       (active-user context query))]
    (some-> result .-organization .-places first)))
