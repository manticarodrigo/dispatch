(ns api.util.crypto
  (:require ["bcryptjs" :as bcrypt]
            [api.config :as config]))

(defn short-code []
  (+ (rand-int 900000) 100000))

(defn encrypt-string [string]
  (.hash bcrypt string config/SALT))
