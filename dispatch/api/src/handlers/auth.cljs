(ns handlers.auth
  (:require
   ["bcryptjs$default" :as bcrypt]
   [cljs-bean.core :refer (->js)]
   [promesa.core :as p]
   [config]
   [model.user :refer (User)]
   [util.anom :as anom]
   [util.express :refer (extract-params)]
   [util.sequelize :refer (parse-error)]))

(defn register
  [req res]
  (->
   (p/let [payload (extract-params
                    [{:type :body :name "firstName"}
                     {:type :body :name "lastName"}
                     {:type :body :name "email"}
                     {:type :body :name "password"}] req)
           {:keys [password]} payload
           encrypted-password (when password (.hash bcrypt password config/APP_SALT))
           user (.create User (->js (assoc payload :password encrypted-password)))]
     (-> res (.status 200) (.send user)))
   (p/catch
    (fn [e]
      (let [[status body] (parse-error e)]
        (-> res (.status status) (.send (->js body))))))))

(defn login
  [req res]
  (->
   (p/let [payload (extract-params
                    [{:type :body :name "email"}
                     {:type :body :name "password"}] req)
           {:keys [email password]} payload
           user (when email (.findOne User (->js {:where {:email email}})))
           encrypted-password (when password (.hash bcrypt password config/APP_SALT))
           password-matches? (= (some-> user .-password) encrypted-password)]
     (cond
       (not user) (-> res (.status 404) (.send (->js (anom/not-found :account-not-found))))
       (not password-matches?) (-> res (.status 401) (.send (->js (anom/forbidden :invalid-password))))
       :else (-> res (.status 200) (.send user))))
   (p/catch
    (fn [e]
      (let [[status body] (parse-error e)]
        (-> res (.status status) (.send (->js body))))))))
