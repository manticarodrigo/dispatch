(ns api.models.payment
  (:require [api.lib.stripe :as stripe]))

(defn create-customer [email]
  (stripe/create-customer email))

(defn create-setup-intent [customer-id]
  (stripe/create-setup-intent customer-id))

(defn find-setup-intent [intent-id]
  (stripe/find-setup-intent intent-id))
