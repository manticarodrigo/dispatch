(ns models.user
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [config]
   [util.sequelize :refer (append)]
   [util.crypto :refer (encrypt-string random-hex)]))

(defn create [context payload]
  (p/let [{:keys [password]} payload
          model (.. context -models -user)
          encrypted-password (when password (encrypt-string password))
          session-id (random-hex)
          _ (.create model (->js (-> payload
                                     (assoc :password encrypted-password)
                                     (assoc :sessions [session-id]))))]
    session-id))

(defn create-session [context payload]
  (p/let [{:keys [user-id user-password password]} payload
          model (.. context -models -user)
          sequelize (.. context -sequelize)
          encrypted-password (when password (encrypt-string password))
          password-matches? (= user-password encrypted-password)
          session-id (when password-matches? (random-hex))
          _ (append sequelize model "sessions" session-id {:id user-id})]
    session-id))

(defn find-by-email [context email]
  (let [model (.. context -models -user)]
    (.findOne model (->js {:where {:email email}}))))
