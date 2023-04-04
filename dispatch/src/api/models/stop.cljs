(ns api.models.stop
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]))

(defn create-arrival [^js context {:keys [stopId note status]}]
  (prisma/update!
   (.. context -prisma -stop)
   {:where {:id stopId}
    :data {:note note
           :status status
           :finishedAt (js/Date.)}}))

(defn stop-query [stop-id]
  {:tasks {:where {:stops {:some {:id stop-id}}}
           :include {:stops {:where {:id stop-id}
                             :include {:place true}}}}})

(defn fetch-organization-stop [^js context {:keys [stopId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include
                                                (stop-query stopId)}}})
          ^js tasks (.. user -organization -tasks)
          ^js first-task (some-> tasks first)
          ^js stops (.. first-task -stops)
          ^js first-stop (some-> stops first)]
    first-stop))

(defn fetch-agent-stop [^js context {:keys [stopId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                (stop-query stopId)}}})
          ^js tasks (.. user -agent -tasks)
          ^js first-task (some-> tasks first)
          ^js stops (.. first-task -stops)
          ^js first-stop (some-> stops first)]
    first-stop))
