(ns api.models.plan
  (:require [promesa.core :as p]
            [cljs-bean.core :refer (->clj)]
            [api.util.prisma :as prisma]
            [api.models.user :as user]
            [api.lib.google.optimization :as optimization]))

(defn create-plan [^js context {:keys [depotId startAt endAt breaks vehicleIds shipmentIds result]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)
          payload {:data {:depot {:connect {:id depotId}}
                          :startAt startAt
                          :endAt endAt
                          :breaks breaks
                          :vehicles {:create (map-indexed
                                              (fn [idx vehicleId]
                                                {:order idx
                                                 :vehicle {:connect {:id vehicleId}}})
                                              vehicleIds)}
                          :shipments {:create  (map-indexed
                                                (fn [idx shipmentId]
                                                  {:order idx
                                                   :shipment {:connect {:id shipmentId}}})
                                                shipmentIds)}
                          :organization {:connect {:id organization-id}}}}]
    (prisma/create!
     (.. context -prisma -plan)
     (if result
       (assoc-in payload [:data :result] result)
       payload))))

(def plan-include {:depot true
                   :vehicles
                   {:orderBy {:order "asc"}
                    :include {:vehicle true}}
                   :shipments
                   {:orderBy {:order "asc"}
                    :include
                    {:shipment {:include
                                {:place true
                                 :stop true
                                 :windows true}}}}})

(def plans-include
  {:plans
   {:include
    plan-include}})

(defn plan-query-include [plan-id]
  {:depot true
   :vehicles
   {:orderBy {:order "asc"}
    :include
    {:vehicle
     {:include
      {:tasks
       {:where {:plan {:id plan-id}}}}}}}
   :shipments
   {:orderBy {:order "asc"}
    :include
    {:shipment
     {:include {:place true
                :stop true
                :windows true}}}}})

(defn plan-query [plan-id]
  {:plans {:where {:id plan-id}
           :include (plan-query-include plan-id)}})

(defn fetch-organization-plans [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include plans-include}}})]
    (.. user -organization -plans)))

(defn fetch-organization-plan [^js context {:keys [planId]}]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include (plan-query planId)}}})
          ^js plan (first (.. user -organization -plans))]
    (set! (.. plan -result) (optimization/parse-result plan))
    plan))

(defn optimize-plan [^js context {:keys [planId]}]
  (p/let [^js plan (prisma/find-unique
                    (.. context -prisma -plan)
                    {:where {:id planId}
                     :include plan-include})
          result (-> (optimization/optimize-plan plan)
                     (.then #(.. % -data))
                     (.catch #(throw (.. % -response -data))))
          ^js updated-plan (prisma/update!
                            (.. context -prisma -plan)
                            {:where {:id planId}
                             :data {:result result}
                             :include plan-include})]
    (set! (.. updated-plan -result) (optimization/parse-result updated-plan))
    updated-plan))

(defn create-plan-tasks [^js context {:keys [planId assignments]}]
  (p/let [^js user (user/active-user context {:include {:organization {:include (plan-query planId)}}})
          organization-id (.. user -organization -id)
          ^js plan (first (.. user -organization -plans))
          routes (-> (optimization/parse-result plan) ->clj :routes)
          ^js updated-plan (prisma/update!
                            (.. context -prisma -plan)
                            {:where {:id planId}
                             :data {:tasks
                                    {:create (map
                                              (fn [{:keys [agentId routeIndex]}]
                                                (let [vehicleId (-> (nth routes routeIndex) :vehicle :id)
                                                      visits (->> (nth routes routeIndex) :visits
                                                                  (map (fn [visit]
                                                                         (let [{:keys [depot shipment]} visit]
                                                                           (if depot
                                                                             {:placeId (:id depot)}
                                                                             {:placeId (-> shipment :place :id)
                                                                              :shipmentId (-> shipment :id)})))))]
                                                  {:route {}
                                                   :startAt (js/Date.)
                                                   :organization {:connect {:id organization-id}}
                                                   :agent {:connect {:id agentId}}
                                                   :vehicle {:connect {:id vehicleId}}
                                                   :stops {:create (map-indexed
                                                                    (fn [idx visit]
                                                                      (let [{:keys [placeId shipmentId]} visit]
                                                                        (if shipmentId
                                                                          {:order idx
                                                                           :place {:connect {:id placeId}}
                                                                           :shipment {:connect {:id shipmentId}}}
                                                                          {:order idx
                                                                           :place {:connect {:id placeId}}})))
                                                                    visits)}}))
                                              assignments)}}
                             :include (plan-query-include planId)})]
    (set! (.. updated-plan -result) (optimization/parse-result updated-plan))
    updated-plan))
