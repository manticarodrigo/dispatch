(ns api.models.user
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]
   [api.util.crypto :refer (encrypt-string random-hex)]))

(defn create [^js context payload]
  (p/let [{:keys [password]} payload
          model (.. context -prisma -user)
          encrypted-password (when password (encrypt-string password))
          session-id (random-hex)
          _ (prisma/create model (merge
                                  (prisma/select-params
                                   payload
                                   [:firstName :lastName :email])
                                  {:password encrypted-password
                                   :sessions [session-id]}))]
    session-id))

(defn create-session [^js context payload]
  (p/let [{:keys [user-id user-password password]} payload
          model (.. context -prisma -user)
          encrypted-password (when password (encrypt-string password))
          password-matches? (= user-password encrypted-password)
          session-id (when password-matches? (random-hex))
          _ (when session-id
              (prisma/push model {:id user-id} "sessions" session-id))]
    session-id))

(defn find-unique [^js context payload]
  (let [model (.. context -prisma -user)
        where (prisma/select-params payload [:id :email])]
    (prisma/find-unique-where model where)))

(defn delete [^js context payload]
  (let [model (.. context -prisma -user)
        where (prisma/select-params payload [:id :email])]
    (-> (prisma/delete-where model where)
        (.then #(.. % -id)))))
