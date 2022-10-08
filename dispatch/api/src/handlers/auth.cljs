(ns handlers.auth
  (:require
   [promesa.core :as p]
   [config]
   [model.user]
   [util.anom :as anom]
   [util.express :refer (extract-params send handle-error-factory)]))

(defn register
  [req res]
  (->
   (p/let [payload (extract-params
                    [{:type :body :name "firstName"}
                     {:type :body :name "lastName"}
                     {:type :body :name "email"}
                     {:type :body :name "password"}] req)
           user (model.user/create payload)]
     (send res 200 user))
   (p/catch (handle-error-factory res))))

(defn login
  [req res]
  (->
   (p/let [payload (extract-params
                    [{:type :body :name "email"}
                     {:type :body :name "password"}] req)
           {:keys [email password]} payload
           user (when email (model.user/find-by-email email))
           session-id (model.user/create-session {:user-id (some-> user .-id)
                                                  :user-password (some-> user .-password)
                                                  :password password})]
     (cond
       (not user) (send res 404 (anom/not-found :account-not-found))
       (not session-id) (send res 401 (anom/forbidden :invalid-password))
       :else (send res 200 {:sessionId session-id})))
   (p/catch (handle-error-factory res))))
