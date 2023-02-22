(ns api.models.stripe
  (:require [promesa.core :as p]
            [api.lib.stripe :as stripe]
            [api.models.user :refer (active-user)]))

(defn find-customer-id [^js context]
  (p/let [^js user (active-user
                    context
                    {:include
                     {:organization
                      {:include
                       {:stripe true}}}})]
    (some-> user .-organization .-stripe .-customerId)))


(defn find-payment-methods [^js context]
  (p/let [customer-id (find-customer-id context)]
    (stripe/list-payment-methods customer-id)))

(defn create-setup-intent [^js context]
  (p/let [customer-id (find-customer-id context)]
    (stripe/create-setup-intent customer-id)))
