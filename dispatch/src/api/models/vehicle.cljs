(ns api.models.vehicle
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]))

(def vehicles-include
  {:vehicles {:where {:archived false}
              :orderBy {:createdAt "desc"}}})

(defn create-vehicles [^js context {:keys [vehicles]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)]
    (prisma/update!
     (.. context -prisma -organization)
     {:where {:id organization-id}
      :data {:vehicles {:create vehicles}}
      :include vehicles-include})))

(defn create-vehicle [^js context vehicle]
  (create-vehicles context {:vehicles [vehicle]}))

(defn archive-vehicles [^js context {:keys [vehicleIds]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)]
    (prisma/update!
     (.. context -prisma -organization)
     {:where {:id organization-id}
      :data {:vehicles {:updateMany {:where {:id {:in vehicleIds}}
                                     :data {:archived true}}}}
      :include vehicles-include})))

(defn fetch-organization-vehicles [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include vehicles-include}}})]
    (.. user -organization -vehicles)))
