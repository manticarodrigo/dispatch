(ns api.util.crypto
  (:require ["crypto" :as crypto]
            ["bcryptjs" :as bcrypt]
            [api.config :as config]))

(defn random-hex []
  (-> crypto (.randomBytes 32) (.toString "hex")))

(defn encrypt-string [string]
  (.hash bcrypt string config/SALT))
