(ns api.models.shipment
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]))

(defn create-shipment [^js context {:keys [placeId size duration windows]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)]
    (prisma/create!
     (.. context -prisma -shipment)
     {:data {:size size
             :duration duration
             :windows windows
             :place {:connect {:id placeId}}
             :organization {:connect {:id organization-id}}}})))

(def shipments-include
  {:shipments {:include {:place true}}})

(defn fetch-organization-shipments [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include shipments-include}}})]
    (.. user -organization -shipments)))