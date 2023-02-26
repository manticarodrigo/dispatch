(ns api.models.user
  (:require
   [promesa.core :as p]
   [api.lib.stripe :as stripe]
   [api.lib.notification :as notification]
   [api.util.prisma :as prisma]
   [api.util.crypto :as crypto]
   [api.util.anom :as anom]
   [api.filters.core :as filters]))

(defn create-user [^js context {:keys [email password organization]}]
  (p/let [encrypted-password (when password (crypto/encrypt-string password))
          ^js customer (stripe/create-customer email)
          ^js organization (prisma/create!
                            (.. context -prisma -organization)
                            {:data {:name organization
                                    :stripe
                                    {:create
                                     {:customerId (.-id customer)}}
                                    :admin
                                    {:create
                                     {:email email
                                      :password encrypted-password
                                      :sessions {:create [{}]}}}}
                             :include {:admin {:include {:sessions true}}}})]
    (some-> organization .-admin .-sessions last .-id)))


(defn login-phone [^js context {:keys [phone]}]
  (p/let [^js user (prisma/find-unique
                    (.. context -prisma -user)
                    {:where {:phone phone}})
          ^js verification (prisma/create!
                            (.. context -prisma -verification)
                            {:data {:code (crypto/short-code)
                                    :user {:connect {:id (.. user -id)}}}})]
    (when-not user
      (throw (anom/gql (anom/not-found :account-not-found))))
    (notification/send-sms phone (str "Your verification code is " (.-code verification)))
    true))

(defn login-confirm [^js context {:keys [code]}]
  (p/let [^js verification (prisma/find-unique
                            (.. context -prisma -verification)
                            {:where {:code code}
                             :include {:user true}})
          verification-id (some-> verification .-id)
          user-id (some-> verification .-user .-id)]
    (when-not verification
      (throw (anom/gql (anom/not-found :verification-not-found))))
    (p/do
      (prisma/delete! (.. context -prisma -verification)
                      {:where {:id verification-id}})
      (-> (prisma/create! (.. context -prisma -session)
                          {:data {:user {:connect {:id user-id}}}})
          (.then (fn [^js session]
                   (.. session -id)))))))

(defn create-session [^js context {:keys [user-id user-password password]}]
  (p/let [encrypted-password (when password (crypto/encrypt-string password))
          password-matches? (= user-password encrypted-password)
          ^js user (when password-matches?
                     (prisma/update! (.. context -prisma -user)
                                     {:where {:id user-id}
                                      :data {:sessions {:create [{}]}}
                                      :include {:sessions true}}))]
    (some-> user .-sessions last .-id)))

(defn find-by-email [^js context email]
  (prisma/find-unique (.. context -prisma -user)
                      {:where {:email email}}))

(defn active-user
  ([^js context]
   (-> (prisma/find-first (.. context -prisma -user)
                          {:where (filters/session (.. context -session))})
       (.then #(or % (throw (anom/gql (anom/forbidden :invalid-session)))))))
  ([^js context query]
   (-> (prisma/find-first (.. context -prisma -user)
                          (merge
                           {:where (filters/session (.. context -session))}
                           query))
       (.then #(or % (throw (anom/gql (anom/forbidden :invalid-session))))))))

(defn fetch-scope [^js context]
  (-> (active-user context {:include {:organization true
                                      :agent true}})
      (.then #(cond
                (.. ^js % -organization) "organization"
                (.. ^js % -agent) "agent"
                :else (throw (anom/gql (anom/forbidden :invalid-session)))))))
