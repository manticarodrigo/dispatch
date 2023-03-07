(ns api.models.plan
  (:require [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]
            [api.lib.mapbox.optimization :as optimization]))

(defn create-plan [^js context {:keys [depotId startAt endAt breaks vehicleIds shipmentIds]}]
  (p/let [^js user (user/active-user context {:include {:organization true}})
          organization-id (.. user -organization -id)]
    (prisma/create!
     (.. context -prisma -plan)
     {:data {:depot {:connect {:id depotId}}
             :startAt startAt
             :endAt endAt
             :breaks breaks
             :vehicles {:connect (mapv #(hash-map :id %) vehicleIds)}
             :shipments {:connect (mapv #(hash-map :id %) shipmentIds)}
             :organization {:connect {:id organization-id}}}})))

(def plan-include {:depot true
                   :vehicles true
                   :shipments
                   {:include
                    {:place true}}})
(def plans-include
  {:plans
   {:include
    plan-include}})

(defn fetch-organization-plans [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include plans-include}}})]
    (.. user -organization -plans)))

(defn optimize-plan [^js context {:keys [planId]}]
  (p/let [^js plan (prisma/find-unique
                    (.. context -prisma -plan)
                    {:where {:id planId}
                     :include plan-include})]))
          ;; _ (->
          ;;    (optimization/optimize-plan plan)
          ;;    (.then #(js/console.log (.. % -data)))
          ;;    (.catch #(js/console.log (.. % -response -data))))
          ;; _ (->
          ;;    (optimization/fetch-plan "0649c462-f345-4703-823a-b8963a86485d")
          ;;    (.then #(js/console.log (.. % -data)))
          ;;    (.catch #(js/console.log (.. % -response -data))))

(defn create-plan-tasks [^js context {:keys [input]}]
  (p/let [{:keys [planId assignments]} input
          ^js user (user/active-user context {:include
                                              {:organization
                                               {:include
                                                {:shipments
                                                 {:include
                                                  {:place true}}}}}})
          place-map (into {} (for [^js shipment (.. user -organization -shipments)]
                               {(.-id shipment) (.. shipment -place -id)}))
          organization-id (.. user -organization -id)]

    (prisma/update!
     (.. context -prisma -plan)
     {:where {:id planId}
      :data {:tasks
             {:create (map
                       (fn [{:keys [agentId vehicleId shipmentIds]}]
                         {:organization {:connect {:id organization-id}}
                          :agent {:connect {:id agentId}}
                          :vehicle {:connect {:id vehicleId}}
                          :route {}
                          :startAt (js/Date.)
                          :stops {:create (map-indexed
                                           (fn [idx id]
                                             {:place {:connect {:id (get place-map id)}}
                                              :shipment {:connect {:id id}}
                                              :order idx})
                                           shipmentIds)}})
                       assignments)}}})))
