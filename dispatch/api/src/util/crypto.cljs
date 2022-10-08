(ns util.crypto
  (:require ["crypto$default" :as crypto]
            ["bcryptjs$default" :as bcrypt]
            [config]))

(defn random-hex []
  (-> crypto (.randomBytes 32) (.toString "hex")))

(defn encrypt-string [string]
  (.hash bcrypt string config/APP_SALT))
