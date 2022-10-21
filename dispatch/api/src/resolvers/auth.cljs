(ns resolvers.auth
  (:require

   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [config]
   [util.anom :as anom]
   [models.user]))

;; (defn register
;;   [req res]
;;   (->
;;    (p/let [payload (extract-params
;;                     [{:type :body :name "firstName"}
;;                      {:type :body :name "lastName"}
;;                      {:type :body :name "email"}
;;                      {:type :body :name "password"}] req)
;;            session-id (model.user/create payload)]
;;      (send res 200 {:sessionId session-id}))
;;    (p/catch (handle-error-factory res))))

(defn login
  [parent args context info]
  (p/let [{:keys [email password]} (->clj args)
          user (when email (models.user/find-by-email context email))
          session-id (models.user/create-session context {:user-id (some-> user .-id)
                                                          :user-password (some-> user .-password)
                                                          :password password})]
    (cond
      (not user) (anom/gql (anom/not-found :account-not-found))
      (not session-id) (anom/gql (anom/forbidden :invalid-password))
      :else session-id)))
