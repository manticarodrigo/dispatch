(ns api.resolvers.user
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.user :as model]))

(defn create-user
  [_ args context _]
  (model/create context (->clj args)))

(defn login-user
  [_ args context _]
  (p/let [{:keys [email password]} (->clj args)
          ^js user (when email (model/find-unique context {:email email}))
          session-id (when user (model/create-session
                                 context
                                 {:user-id (some-> user .-id)
                                  :user-password (some-> user .-password)
                                  :password password}))]
    (cond
      (not user) (anom/gql (anom/not-found :account-not-found))
      (not session-id) (anom/gql (anom/incorrect :invalid-password))
      :else session-id)))

(defn logged-in-user
  [_ _ context _]
  (model/logged-in-user context))
