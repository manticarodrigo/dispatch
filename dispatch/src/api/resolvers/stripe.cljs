(ns api.resolvers.stripe
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.stripe :as stripe]))

(defn find-payment-methods
  [_ _ context _]
  (stripe/find-payment-methods context))

(defn create-setup-intent
  [_ _ context _]
  (stripe/create-setup-intent context))

(defn detach-payment-method
  [_ args context _]
  (stripe/detach-payment-method context (->clj args)))
