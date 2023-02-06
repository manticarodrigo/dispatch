(ns api.resolvers.user
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.user :as user]))

(defn create-user
  [_ args context _]
  (user/create context (->clj args)))

(defn login-user
  [_ args context _]
  (p/let [{:keys [email password]} (->clj args)
          ^js user (when email (user/find-by-email context email))
          session-id (when user (user/create-session
                                 context
                                 {:user-id (some-> user .-id)
                                  :user-password (some-> user .-password)
                                  :password password}))]
    (cond
      (not user) (anom/gql (anom/not-found :account-not-found))
      (not session-id) (anom/gql (anom/incorrect :invalid-password))
      :else session-id)))

(defn active-user
  [_ _ context _]
  (user/active-user context))
