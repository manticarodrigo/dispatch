(ns api.models.user
  (:require [reagent.dom.server :refer (render-to-string)]
            [promesa.core :as p]
            [api.config :as config]
            [api.lib.stripe :as stripe]
            [api.lib.gmail :as gmail]
            [api.lib.notification :as notification]
            [api.util.prisma :as prisma]
            [api.util.crypto :as crypto]
            [api.util.anom :as anom]
            [api.filters.core :as filters]))

(defn send-verification-mail [email ^js verification]
  (let [code (.-code verification)
        text (str "Your Ambito Dispatch verification code is: " code)]
    (if (= config/STAGE "test")
      code
      (p/do
        (gmail/send-mail
         {:to email
          :subject "Verification code"
          :text text
          :html (render-to-string [:p text])
          :textEncoding "base64"})
        nil))))

(defn send-verification-sms [phone ^js verification]
  (let [code (.-code verification)
        text (str "Your Ambito Dispatch verification code is: " code)]
    (if (= config/STAGE "test")
      code
      (p/do
        (notification/send-sms phone text)
        nil))))

(defn register-user [^js context {:keys [email password organization]}]
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
                             :include {:admin {:include {:sessions true}}}})
          ^js admin (.. organization -admin)
          ^js verification (when admin
                             (prisma/create!
                              (.. context -prisma -verification)
                              {:data {:code (crypto/short-code)
                                      :user {:connect {:id (.. admin -id)}}}}))]
    (send-verification-mail email verification)))

(defn login-password [^js context {:keys [email password]}]
  (p/let [^js user (when email (prisma/find-unique
                                (.. context -prisma -user)
                                {:where {:email email}}))
          user-id (some-> user .-id)
          user-password (some-> user .-password)
          encrypted-password (when password (crypto/encrypt-string password))
          password-matches? (= user-password encrypted-password)
          ^js updated-user (when password-matches?
                             (prisma/update! (.. context -prisma -user)
                                             {:where {:id user-id}
                                              :data {:sessions {:create [{}]}}
                                              :include {:sessions true}}))
          session-id (some-> updated-user .-sessions last .-id)]
    (cond
      (not user) (anom/gql (anom/not-found :account-not-found))
      (not session-id) (anom/gql (anom/incorrect :invalid-password))
      :else session-id)))

(defn login-email [^js context {:keys [email]}]
  (p/let [^js user (prisma/find-unique
                    (.. context -prisma -user)
                    {:where {:email email}})
          ^js verification (when user
                             (prisma/create!
                              (.. context -prisma -verification)
                              {:data {:code (crypto/short-code)
                                      :user {:connect {:id (.. user -id)}}}}))]
    (when-not user
      (throw (anom/gql (anom/not-found :account-not-found))))
    (send-verification-mail email verification)))

(defn login-phone [^js context {:keys [phone]}]
  (p/let [^js user (prisma/find-unique
                    (.. context -prisma -user)
                    {:where {:phone phone}})
          ^js verification (when user
                             (prisma/create!
                              (.. context -prisma -verification)
                              {:data {:code (crypto/short-code)
                                      :user {:connect {:id (.. user -id)}}}}))]
    (when-not user
      (throw (anom/gql (anom/not-found :account-not-found))))
    (send-verification-sms phone verification)))

(defn login-user [^js context {:keys [email] :as args}]
  (if email
    (login-email context args)
    (login-phone context args)))

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
