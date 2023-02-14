(ns api.models.place
  (:require [promesa.core :as p]
            [goog.object :as gobj]
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
                         :query {:include {:user true}}})]
        (prisma/create!
         (.. context -prisma -place)
         (update-in payload [:data] merge
                    {:agent {:connect {:id agentId}}
                     :user {:connect {:id (.. agent -user -id)}}})))
      (p/let [^js user (active-user context)]
        (prisma/create!
         (.. context -prisma -place)
         (update-in payload [:data] merge
                    {:user {:connect {:id (.. user -id)}}}))))))

(defn find-all [^js context {:keys [agentId deviceId]}]
  (if agentId
    (p/-> (active-agent
           context
           {:agentId agentId
            :deviceId deviceId
            :query {:include
                    {:user
                     {:include {:places {:orderBy {:name "asc"}}}}}}})
          (gobj/get "user")
          (gobj/get "places"))
    (p/-> (active-user context {:include {:places {:orderBy {:name "asc"}}}})
          (gobj/get "places"))))

(defn find-unique [^js context {:keys [agentId deviceId placeId]}]
  (if agentId
    (p/-> (active-agent
           context
           {:agentId agentId
            :deviceId deviceId
            :query {:include
                    {:user
                     {:include {:places {:where {:id placeId}}}}}}})
          (gobj/get "user")
          (gobj/get "places")
          first)
    (p/-> (active-user context {:include {:places {:where {:id placeId}}}})
          (gobj/get "places")
          first)))
