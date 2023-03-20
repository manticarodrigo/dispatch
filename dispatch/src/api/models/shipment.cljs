(ns api.models.shipment
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]))

(def shipments-include
  {:shipments {:where {:archived false}
               :orderBy {:createdAt "desc"}
               :include {:place true
                         :windows true}}})

(defn create-shipments [^js context {:keys [shipments]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)
          _ (.$transaction
             (.. context -prisma)
             (apply
              array
              (mapv
               (fn [{:keys [externalId externalPlaceId placeId weight volume duration windows]}]
                 (let [payload {:place {:connect (if externalPlaceId
                                                   {:externalId_organizationId
                                                    {:externalId externalPlaceId
                                                     :organizationId organization-id}}
                                                   {:id placeId})}
                                :weight weight
                                :volume volume
                                :duration duration}]
                   (if externalId
                     (prisma/upsert!
                      (.. context -prisma -shipment)
                      (merge
                       {:where {:externalId_organizationId
                                {:externalId externalId
                                 :organizationId organization-id}}
                        :create (merge {:organization {:connect {:id organization-id}}}
                                       (when externalId {:externalId externalId})
                                       (when windows {:windows {:create windows}})
                                       payload)
                        :update (merge {:archived false
                                        :windows {:deleteMany {}
                                                  :create (or windows [])}}
                                       payload)}))
                     (prisma/update!
                      (.. context -prisma -organization)
                      {:where {:id organization-id}
                       :data {:shipments
                              {:create [(merge (when windows {:windows {:create windows}})
                                               payload)]}}}))))
               shipments)))]
    (user/active-user context {:include
                               {:organization
                                {:include shipments-include}}})))

(defn create-shipment [^js context {:keys [shipment]}]
  (create-shipments context {:shipments [shipment]}))

(defn archive-shipments [^js context {:keys [shipmentIds]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)]
    (prisma/update!
     (.. context -prisma -organization)
     {:where {:id organization-id}
      :data {:shipments {:updateMany {:where {:id {:in shipmentIds}}
                                      :data {:archived true}}}}
      :include shipments-include})))

(defn fetch-organization-shipments [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include shipments-include}}})]
    (.. user -organization -shipments)))
