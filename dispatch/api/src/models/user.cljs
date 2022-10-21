(ns models.user
  (:require
   ["sequelize" :refer (DataTypes)]
   [clojure.core :refer (atom)]
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [config]
   [util.sequelize :refer (gen-id append)]
   [util.crypto :refer (encrypt-string random-hex)]))

(defn init [sequelize]
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

(defn get-model-instance [context]
  (.. context -models -user))

(defn create [context payload]
  (p/let [{:keys [password]} payload
          instance (get-model-instance context)
          encrypted-password (when password (encrypt-string password))
          session-id (random-hex)
          _ (.create instance (->js (-> payload
                                        (assoc :password encrypted-password)
                                        (assoc :sessions [session-id]))))]
    session-id))

(defn create-session [context payload]
  (p/let [{:keys [user-id user-password password]} payload
          instance (get-model-instance context)
          encrypted-password (when password (encrypt-string password))
          password-matches? (= user-password encrypted-password)
          session-id (when password-matches? (random-hex))
          _ (append instance "sessions" session-id {:id user-id})]
    session-id))

(defn find-by-email [context email]
  (let [instance (get-model-instance context)]
    (.findOne instance (->js {:where {:email email}}))))
