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
                                 :pickup true
                                 :delivery true
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
                :pickup true
                :delivery true
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
          ^js updated-plan (prisma/update!
                            (.. context -prisma -plan)
                            {:where {:id planId}
                             :data {:tasks
                                    {:create (map
                                              (fn [{:keys [agentId routeIndex]}]
                                                (let [route (-> plan .-result .-routes (nth routeIndex) ->clj)
                                                      vehicle-idx (-> route :vehicleIndex (or 0))
                                                      vehicle-id (-> (.. plan -vehicles) ^js (nth vehicle-idx) .-vehicle .-id)
                                                      merged-stops (optimization/merge-stops route)]
                                                  {:route route
                                                   :startAt (-> route :vehicleStartTime)
                                                   :organization {:connect {:id organization-id}}
                                                   :agent {:connect {:id agentId}}
                                                   :vehicle {:connect {:id vehicle-id}}
                                                   :stops {:create (map-indexed
                                                                    (fn [idx {:keys [visits]}]
                                                                      (let [first? (= idx 0)
                                                                            last? (= idx (dec (count merged-stops)))
                                                                            shipments (->> visits
                                                                                           (map #(or (:shipmentIndex %) 0))
                                                                                           (map #(-> plan .-shipments ^js (nth %) .-shipment)))
                                                                            ^js first-shipment (first shipments)]
                                                                        (if (or first? last?)
                                                                          {:order idx
                                                                           :place {:connect
                                                                                   {:id (-> plan .-depot .-id)}}
                                                                           :pickups {:connect
                                                                                     (map (fn [^js shipment]
                                                                                            {:id (.-id shipment)})
                                                                                          shipments)}}
                                                                          {:order idx
                                                                           :place {:connect
                                                                                   {:id (-> first-shipment .-place .-id)}}
                                                                           :deliveries {:connect
                                                                                        (map (fn [^js shipment]
                                                                                               {:id (.-id shipment)})
                                                                                             shipments)}})))
                                                                    merged-stops)}}))
                                              assignments)}}
                             :include (plan-query-include planId)})]
    (set! (.. updated-plan -result) (optimization/parse-result updated-plan))
    updated-plan))
