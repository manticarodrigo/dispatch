(ns api.models.place
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]))

(defn create [^js context {:keys [name phone email description lat lng]}]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                {:organization true}}
                                               :organization true}})
          ^js organization (or (.. user -organization)
                               (.. user -agent -organization))
          payload {:data {:name name
                          :phone phone
                          :email email
                          :description description
                          :lat lat
                          :lng lng
                          :organization {:connect {:id (.. organization -id)}}}}]
    (prisma/create!
     (.. context -prisma -place)
     (if (.. user -agent)
       (assoc-in payload [:data :agent] {:connect {:id (.. user -agent -id)}})
       payload))))

(defn find-all [^js context]
  (p/let [query {:include
                 {:places {:orderBy {:name "asc"}}}}
          ^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                {:organization query}}
                                               :organization query}})
          ^js organization (or (.. user -organization)
                               (.. user -agent -organization))]
    (.. organization -places)))

(defn find-unique [^js context {:keys [placeId]}]
  (p/let [query {:include
                 {:places {:where {:id placeId}}}}
          ^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                {:organization query}}
                                               :organization query}})
          ^js organization (or (.. user -organization)
                               (.. user -agent -organization))]
    (first (.. organization -places))))
