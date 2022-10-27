(ns api.models.user
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [api.util.sequelize :refer (append)]
   [api.util.crypto :refer (encrypt-string random-hex)]))

(defn create [^js context payload]
  (p/let [{:keys [password]} payload
          model (.. context -models -user)
          encrypted-password (when password (encrypt-string password))
          session-id (random-hex)
          _ (.create model (->js (-> payload
                                     (assoc :password encrypted-password)
                                     (assoc :sessions [session-id]))))]
    session-id))

(defn create-session [^js context payload]
  (p/let [{:keys [user-id user-password password]} payload
          model (.. context -models -user)
          sequelize (.. context -sequelize)
          encrypted-password (when password (encrypt-string password))
          password-matches? (= user-password encrypted-password)
          session-id (when password-matches? (random-hex))
          _ (append sequelize model "sessions" session-id {:id user-id})]
    session-id))

(defn find-by-id [^js context payload]
  (let [{:keys [id]} payload
        ^js model (.. context -models -user)]
    (.findOne model (->js {:where {:id id}}))))

(defn find-by-email [^js context payload]
  (let [{:keys [email]} payload
        ^js model (.. context -models -user)]
    (.findOne model (->js {:where {:email email}}))))

(defn delete [context payload]
  (p/let [;; user (find-by-id context payload)
          ^js user (find-by-email context payload)
          id (.. user -id)
          _ (.destroy user)]
    id))
