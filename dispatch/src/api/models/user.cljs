(ns api.models.user
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]
   [api.util.crypto :refer (encrypt-string)]
   [api.util.anom :as anom]
   [api.filters.core :as filters]))

(defn create [^js context {:keys [email password]}]
  (p/let [encrypted-password (when password (encrypt-string password))
          ^js user (prisma/create!
                    (.. context -prisma -user)
                    {:data {:email email
                            :password encrypted-password
                            :sessions {:create [{}]}}
                     :include {:sessions true}})]
    (some-> user .-sessions last .-id)))

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

(defn logged-in-user
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
