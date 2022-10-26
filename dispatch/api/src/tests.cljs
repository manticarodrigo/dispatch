(ns tests
  (:require ["supertest$default" :as request]
            [cljs.test :as t :refer-macros [deftest async is]]
            [cljs-bean.core :refer (->js)]
            [promesa.core :as p]))

(defn send [body]
  (-> (request "http://localhost:3000")
      (.post "/")
      (.send (->js body))))

(def register-query "
  mutation Register($firstName: String!, $lastName: String!, $email: String!, $password: String!) {
    register(firstName: $firstName, lastName: $lastName, email: $email, password: $password)
  }
")

(deftest register
  (async done
         (p/let [res (send {:query register-query
                            :variables {:firstName "test"
                                        :lastName "test"
                                        :email "test@test.test"
                                        :password "test"}})]
           (is (some? (.. res -body -data -register)))
           (done))))

(def login-query "
  mutation Login($email: String!, $password: String!) {
    login(email: $email, password: $password)
  }
")

(deftest login
  (async done
         (p/let [res (send {:query login-query
                            :variables {:email "test@test.test"
                                        :password "test"}})]
           (is (some? (.. res -body -data -login)))
           (done))))

(def delete-query "
  mutation Delete($email: String!) {
    delete(email: $email)
  }
")

(deftest delete
  (async done
         (p/let [res (send {:query delete-query
                            :variables {:email "test@test.test"}})]
           (is (some? (.. res -body -data -delete)))
           (done))))

(defmethod t/report [:cljs.test/default :begin-test-var] [m]
  (println "===" (-> m :var meta :name))
  (println))

(defn test-ns-hook []
  (p/do
    (register)
    (login)
    (delete)))

(defn run-tests []
  (test-ns-hook))

(comment
  (run-tests))
