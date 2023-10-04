(ns api.models.stripe
  (:require [promesa.core :as p]
            [api.lib.stripe :as stripe]
            [api.models.user :refer (active-user)]))

(defn fetch-customer-id [^js context]
  (p/let [^js user (active-user
                    context
                    {:include
                     {:organization
                      {:include
                       {:stripe true}}}})]
    (.. user -organization -stripe -customerId)))


(defn fetch-payment-methods [^js context]
  (p/let [customer-id (fetch-customer-id context)]
    (stripe/list-payment-methods customer-id)))

(defn create-setup-intent [^js context]
  (p/let [customer-id (fetch-customer-id context)]
    (stripe/create-setup-intent customer-id)))

(defn detach-payment-method [^js context {:keys [paymentMethodId]}]
  (p/let [customer-id (fetch-customer-id context)
          payment-method-id (-> (stripe/list-payment-methods customer-id)
                                (.then (fn [^js methods]
                                         (->> methods
                                              .-data
                                              (filter #(= paymentMethodId (.-id %)))
                                              first
                                              .-id))))
          ^js payment-method (stripe/detach-payment-method payment-method-id)]
    (= (.-id payment-method) payment-method-id)))
