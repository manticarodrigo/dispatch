(ns api.resolvers.stripe
  (:require [api.models.stripe :as stripe]))

(defn find-payment-methods
  [_ _ context _]
  (stripe/find-payment-methods context))

(defn create-setup-intent
  [_ _ context _]
  (stripe/create-setup-intent context))
