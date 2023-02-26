(ns api.models.place
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.filters.core :as filters]
            [api.models.user :as user]))

(defn create-place [^js context {:keys [name phone email description lat lng]}]
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

(def places-query
  {:places {:orderBy {:name "asc"}}})

(defn place-query [place-id filters]
  {:places {:where {:id place-id}
            :include
            {:organization
             {:include
              {:tasks
               {:where
                (update-in
                 (filters/task filters)
                 [:AND]
                 conj
                 {:stops {:some {:place {:id place-id}}}})
                :orderBy {:startAt "asc"}
                :include {:agent true
                          :stops {:orderBy {:order "asc"}
                                  :include
                                  {:place true}}}}}}}}})

(defn fetch-organization-places [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include places-query}}})]
    (.. user -organization -places)))

(defn fetch-agent-places [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                {:organization
                                                 {:include places-query}}}}})]
    (.. user -agent -organization -places)))

(defn fetch-organization-place [^js context {:keys [placeId filters]}]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include (place-query placeId filters)}}})
          ^js place (first (.. user -organization -places))
          ^js tasks (.. place -organization -tasks)]
    (set! (.. place -tasks) tasks)
    place))

(defn fetch-agent-place [^js context {:keys [placeId filters]}]
  (p/let [^js user (user/active-user context {:include
                                              {:agent
                                               {:include
                                                {:organization
                                                 {:include (place-query placeId filters)}}}}})
          ^js place (first (.. user -agent -organization -places))
          ^js tasks (.. place -organization -tasks)]
    (set! (.. place -tasks) tasks)
    place))
