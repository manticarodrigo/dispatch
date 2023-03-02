(ns api.models.vehicle
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]))

(defn create-vehicle [^js context {:keys [name capacities]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)]
    (prisma/create!
     (.. context -prisma -vehicle)
     {:data {:name name
             :capacities capacities
             :organization {:connect {:id organization-id}}}})))

(def vehicles-include
  {:vehicles true})

(defn fetch-organization-vehicles [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include vehicles-include}}})]
    (.. user -organization -vehicles)))
