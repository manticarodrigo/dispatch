(ns model.user
  (:require
   ["sequelize" :refer (DataTypes)]
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [config]
   [util.sequelize :refer (sequelize gen-id append)]
   [util.crypto :refer (encrypt-string random-hex)]))

(def User
  (.define sequelize "User"
           (->js {:id (gen-id)
                  :sessions {:type (.ARRAY DataTypes (.-TEXT DataTypes))}
                  :email {:type (.-TEXT DataTypes)
                          :allowNull false
                          :unique true
                          :validate {:len [6]}}
                  :password {:type (.-TEXT DataTypes)
                             :allowNull false
                             :validate {:len [1]}}
                  :firstName {:type (.-TEXT DataTypes)
                              :allowNull false
                              :validate {:len [1]}}
                  :lastName {:type (.-TEXT DataTypes)
                             :allowNull false
                             :validate {:len [1]}}
                  :imageUrl (.-TEXT DataTypes)
                  :location (.GEOGRAPHY DataTypes "POINT")})))

(defn create [payload]
  (p/let [{:keys [password]} payload
          encrypted-password (when password (encrypt-string password))
          session-id (random-hex)
          _ (.create User (->js (-> payload
                                    (assoc :password encrypted-password)
                                    (assoc :sessions [session-id]))))]
    session-id))

(defn create-session [payload]
  (p/let [{:keys [user-id user-password password]} payload
          encrypted-password (when password (encrypt-string password))
          password-matches? (= user-password encrypted-password)
          session-id (when password-matches? (random-hex))
          _ (append User "sessions" session-id {:id user-id})]
    session-id))

(defn find-by-email [email]
  (.findOne User (->js {:where {:email email}})))
