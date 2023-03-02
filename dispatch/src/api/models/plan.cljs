(ns api.models.plan
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]))

(defn create-plan [^js context {:keys [depotId startAt endAt breaks vehicleIds shipmentIds]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)]
    (prisma/create!
     (.. context -prisma -plan)
     {:data {:depot {:connect {:id depotId}}
             :startAt startAt
             :endAt endAt
             :breaks breaks
             :vehicles {:connect (mapv #(hash-map :id %) vehicleIds)}
             :shipments {:connect (mapv #(hash-map :id %) shipmentIds)}
             :organization {:connect {:id organization-id}}}})))

(def plans-include
  {:plans {:depot true
           :vehicles true
           :shipments true}})

(defn fetch-organization-plans [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include plans-include}}})]
    (.. user -organization -plans)))
