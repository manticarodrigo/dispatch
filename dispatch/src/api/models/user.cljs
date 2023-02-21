(ns api.models.user
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]
   [api.util.crypto :refer (encrypt-string)]
   [api.util.anom :as anom]
   [api.filters.core :as filters]
   [api.models.payment :as payment]))

(defn create [^js context {:keys [email password organization]}]
  (p/let [encrypted-password (when password (encrypt-string password))
          ^js customer (payment/create-customer email)
          ^js setup-intent (payment/create-setup-intent (.-id customer))
          ^js organization (prisma/create!
                            (.. context -prisma -organization)
                            {:data {:name organization
                                    :stripeCustomer
                                    {:create
                                     {:customerId (.-id customer)
                                      :setupIntentId (.-id setup-intent)}}
                                    :admin
                                    {:create
                                     {:email email
                                      :password encrypted-password
                                      :sessions {:create [{}]}}}}
                             :include {:admin {:include {:sessions true}}}})]
    (some-> organization .-admin .-sessions last .-id)))

(defn create-session [^js context {:keys [user-id user-password password]}]
  (p/let [encrypted-password (when password (encrypt-string password))
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
   (p/-> (prisma/find-first (.. context -prisma -user)
                            {:where (filters/session (.. context -session))})
         (or (throw (anom/gql (anom/forbidden :invalid-session))))))
  ([^js context query]
   (p/-> (prisma/find-first (.. context -prisma -user)
                            (merge
                             {:where (filters/session (.. context -session))}
                             query))
         (or (throw (anom/gql (anom/forbidden :invalid-session)))))))

(defn find-setup-intent [^js context]
  (p/let [^js user (active-user
                    context
                    {:include
                     {:organization
                      {:include
                       {:stripeCustomer true}}}})
          intent-id (some-> user
                            .-organization
                            .-stripeCustomer
                            .-setupIntentId)
          ^js setup-intent (payment/find-setup-intent intent-id)]
    (some-> setup-intent .-client_secret)))
