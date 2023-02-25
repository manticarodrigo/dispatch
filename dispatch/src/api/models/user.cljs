(ns api.models.user
  (:require
   [promesa.core :as p]
   [api.lib.stripe :as stripe]
   [api.lib.gmail :as gmail]
   [api.util.prisma :as prisma]
   [api.util.crypto :refer (encrypt-string)]
   [api.util.anom :as anom]
   [api.filters.core :as filters]))

(defn create [^js context {:keys [email password organization]}]
  (p/let [encrypted-password (when password (encrypt-string password))
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
          _ (gmail/send-mail {:to "manticarodrigo@gmail.com"
                              :subject "hello world"
                              :text "hello"
                              :html "<p>hello world!</p>"
                              :textEncoding "base64"})]
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
