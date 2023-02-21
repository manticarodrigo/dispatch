(ns api.lib.stripe
  (:require ["stripe" :as init-stripe]
            [api.config :as config]))

(def ^js stripe (init-stripe config/STRIPE_SECRET_KEY))

(defn create-customer [email]
  (-> stripe .-customers
      (.create #js{:email email})))

(defn create-setup-intent [customer-id]
  (-> stripe .-setupIntents
      (.create #js{:customer customer-id})))

(defn find-setup-intent [intent-id]
  (-> stripe .-setupIntents
      (.retrieve intent-id)))
