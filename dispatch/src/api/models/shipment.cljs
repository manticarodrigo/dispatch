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
          organization-id (.. user -organization -id)]
    (prisma/update!
     (.. context -prisma -organization)
     {:where {:id organization-id}
      :data {:shipments {:create (map
                                  (fn [{:keys [placeId weight volume duration windows]}]
                                    {:place {:connect {:id placeId}}
                                     :windows {:create (or windows [])}
                                     :weight weight
                                     :volume volume
                                     :duration duration})
                                  shipments)}}
      :include shipments-include})))

(defn create-shipment [^js context shipment]
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
