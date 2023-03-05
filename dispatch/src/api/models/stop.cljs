(ns api.models.stop
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]))

(defn create-arrival [^js context {:keys [stopId note]}]
  (prisma/update!
   (.. context -prisma -stop)
   {:where {:id stopId}
    :data {:arrivedAt (js/Date.) :note note}}))

(defn stop-query [stop-id]
  {:tasks {:where {:stops {:some {:id stop-id}}}
           :include {:stops {:where {:id stop-id}
                             :include {:place true}}}}})

(defn fetch-organization-stop [^js context {:keys [stopId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include
                                                (stop-query stopId)}}})]
    (some-> (.. user -organization -tasks) first .-stops first)))

(defn fetch-agent-stop [^js context {:keys [stopId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                (stop-query stopId)}}})]
    (some-> (.. user -agent -tasks) first .-stops first)))
