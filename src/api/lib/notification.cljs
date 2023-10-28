(ns api.lib.notification
  (:require ["twilio" :as twilio]
            [promesa.core :as p]
            [common.config :as config]))

(def client (twilio config/TWILIO_ACCOUNT_SID
                    config/TWILIO_AUTH_TOKEN))

(defn send-sms [phone message]
  (when-not (= config/STAGE "test")
    (p/do
      (.create
       (.-messages client)
       #js{:from config/TWILIO_PHONE_NUMBER
           :to   phone
           :body message}))))
