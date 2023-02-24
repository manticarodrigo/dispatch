(ns api.lib.notification
  (:require ["aws-sdk" :as aws]
            [promesa.core :as p]
            [api.config :as config]))

(aws/config.update #js{:region "us-east-1"})

(def sns (aws/SNS. #js{:apiVersion "2010-03-31"}))

(defn send-sms [phone message]
  (when-not (= config/STAGE "test")
    (p/do
      (-> sns
          (.setSMSAttributes
           #js{:attributes
               #js{:DefaultSMSType "Transactional"}})
          (.promise))
      (-> sns
          (.publish
           #js{:Subject     "Ambito"
               :PhoneNumber phone
               :Message     message})
          (.promise)))))
