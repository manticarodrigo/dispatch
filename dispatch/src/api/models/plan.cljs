(ns api.models.plan
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]
            [api.lib.google.optimization :as optimization]))

(defn create-plan [^js context {:keys [depotId startAt endAt breaks vehicleIds shipmentIds]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)]
    (prisma/create!
     (.. context -prisma -plan)
     {:data {:depot {:connect {:id depotId}}
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
             :organization {:connect {:id organization-id}}}})))

(def plan-include {:depot true
                   :vehicles
                   {:orderBy {:order "asc"}
                    :include {:vehicle true}}
                   :shipments
                   {:orderBy {:order "asc"}
                    :include
                    {:shipment {:include
                                {:place true
                                 :stop true}}}}})

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
                :stop true}}}}})

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

(defn create-plan-tasks [^js context {:keys [input]}]
  (p/let [{:keys [planId assignments]} input
          all-visits (flatten (map :visits assignments))
          all-shipment-ids (flatten (->> all-visits
                                         (map :shipmentId)
                                         (remove nil?)))
          ^js user (user/active-user context {:include
                                              {:organization
                                               {:include
                                                {:shipments
                                                 {:where
                                                  {:id {:in all-shipment-ids}}
                                                  :include
                                                  {:place true}}}}}})
          shipment-place-map (into {} (for [^js shipment (.. user -organization -shipments)]
                                        {(.-id shipment) (.. shipment -place -id)}))
          organization-id (.. user -organization -id)
          ^js updated-plan (prisma/update!
                            (.. context -prisma -plan)
                            {:where {:id planId}
                             :data {:tasks
                                    {:create (map
                                              (fn [{:keys [agentId vehicleId visits]}]
                                                {:route {}
                                                 :startAt (js/Date.)
                                                 :organization {:connect {:id organization-id}}
                                                 :agent {:connect {:id agentId}}
                                                 :vehicle {:connect {:id vehicleId}}
                                                 :stops {:create (map-indexed
                                                                  (fn [idx visit]
                                                                    (let [{:keys [depotId shipmentId]} visit]
                                                                      (if depotId
                                                                        {:order idx
                                                                         :place {:connect {:id depotId}}}
                                                                        {:order idx
                                                                         :place {:connect {:id (get shipment-place-map shipmentId)}}
                                                                         :shipment {:connect {:id shipmentId}}})))
                                                                  visits)}})
                                              assignments)}}
                             :include (plan-query-include planId)})]
    (set! (.. updated-plan -result) (optimization/parse-result updated-plan))
    updated-plan))
