(ns api.models.plan
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [api.util.prisma :as prisma]
            [api.models.user :as user]
            ;; [api.lib.mapbox.optimization :as optimization]
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

(defn plan-query [plan-id]
  {:plans {:where {:id plan-id}
           :include plan-include}})

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
          ;; _ (->
          ;;    (optimization/optimize-plan plan)
          ;;    (.then #(js/console.log (.. % -data)))
          ;;    (.catch #(js/console.log (.. % -response -data))))
          ;; _ (->
          ;;    (optimization/fetch-plan "0649c462-f345-4703-823a-b8963a86485d")
          ;;    (.then #(js/console.log (.. % -data)))
          ;;    (.catch #(js/console.log (.. % -response -data))))
          ]
    (prisma/update!
     (.. context -prisma -plan)
     {:where {:id planId}
      :data {:result (js/JSON.parse (inline "samples/response.json"))}})))

(defn create-plan-tasks [^js context {:keys [input]}]
  (p/let [{:keys [planId assignments]} input
          all-shipment-ids (flatten (map :shipmentIds assignments))
          ^js user (user/active-user context {:include
                                              {:organization
                                               {:include
                                                {:shipments
                                                 {:where
                                                  {:id {:in all-shipment-ids}}
                                                  :include
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
                         {:route {}
                          :startAt (js/Date.)
                          :organization {:connect {:id organization-id}}
                          :agent {:connect {:id agentId}}
                          :vehicle {:connect {:id vehicleId}}
                          :stops {:create (map-indexed
                                           (fn [idx id]
                                             {:order idx
                                              :place {:connect {:id (get place-map id)}}
                                              :shipment {:connect {:id id}}})
                                           shipmentIds)}})
                       assignments)}}})))
