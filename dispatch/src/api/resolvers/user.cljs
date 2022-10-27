(ns api.resolvers.user
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.user :as models.user]))

(defn register
  [_ args context _]
  (-> (p/let [payload (->clj args)
              session-id (models.user/create context payload)]
        session-id)
      (p/catch anom/handle-resolver-error)))

(defn login
  [_ args context _]
  (-> (p/let [{:keys [email password]} (->clj args)
              user (when email (models.user/find-by-email context {:email email}))
              session-id (models.user/create-session context {:user-id (some-> user .-id)
                                                              :user-password (some-> user .-password)
                                                              :password password})]
        (cond
          (not user) (anom/gql (anom/not-found :account-not-found))
          (not session-id) (anom/gql (anom/forbidden :invalid-password))
          :else session-id))
      (p/catch anom/handle-resolver-error)))

(defn delete
  [_ args context _]
  (-> (models.user/delete context {:id (.. args -id)
                                   :email (.. args -email)})
      (p/catch (fn [e] (js/console.log e)))))