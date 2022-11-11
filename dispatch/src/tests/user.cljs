(ns tests.user
  (:require
   [shadow.resource :refer (inline)]
   [cljs.test :as t :refer-macros [deftest async testing is use-fixtures]]
   [promesa.core :as p]
   [tests.util.api :refer (send)]
   [tests.util.ui :as ui :refer (with-mounted-component test-app change submit)]))

(use-fixtures :each
  {:before ui/before
   :after ui/after})

(deftest register-success
  (async done
         (p/let [query (inline "mutations/user/register.graphql")
                 variables {:email "test@test.test" :password "test"}
                 request  {:query query :variables variables}
                 result (send request)]

           (testing "api returns data"
             (is (some-> result :data :register)))

           (with-mounted-component
             [test-app {:route "/register" :mocks [{:request request :result result}]}]
             (fn [^js component]
               (p/do
                 (change (.getByLabelText component "Email") (:email variables))
                 (change (.getByLabelText component "Password") (:password variables))
                 (submit (-> component (.-container) (.querySelector "form")))
                 (-> (.findByText component "Fleet")
                     (.then #(testing "ui form submits and redirects" (is (some? %))))
                     (.then done))))))))

(deftest register-conflict
  (async done
         (p/let [query (inline "mutations/user/register.graphql")
                 variables {:email "test@test.test" :password "test"}
                 request  {:query query :variables variables}
                 result (send request)
                 error (-> result :errors first)
                 anom (-> error :extensions :anom)]

           (testing "api returns unique email anom"
             (is (and
                  (= "conflict" (-> anom :category))
                  (= "unique-constraint" (-> anom :reason))
                  (= "email" (-> anom :meta :target first)))))

           (with-mounted-component
             [test-app {:route "/register" :mocks [{:request request :result result}]}]
             (fn [^js component]
               (p/do
                 (change (.getByLabelText component "Email") (:email variables))
                 (change (.getByLabelText component "Password") (:password variables))
                 (submit (-> component (.-container) (.querySelector "form")))
                 (-> (.findByText component "The account already exists.")
                     (.then #(testing "ui form submits and presents unique email message" (is (some? %))))
                     (.then done))))))))

(deftest login
  (async done
         (let [query (inline "mutations/user/login.graphql")
               payload {:query query
                        :variables {:email "test@test.test"
                                    :password "test"}}]
           (p/do
             (p/let [res (send payload)]
               (testing "returns data"
                 (is (some-> res :data :login))))
             (p/let [res (send (assoc-in payload [:variables :email] "not@found.test"))
                     anom (-> res :errors first :extensions :anom)]
               (testing "returns invalid email anom"
                 (is (= "not-found" (-> anom :category)))
                 (is (= "account-not-found" (-> anom :reason)))))
             (p/let [res (send (assoc-in payload [:variables :password] "incorrect"))
                     anom (-> res :errors first :extensions :anom)]
               (testing "returns invalid password anom"
                 (is (= "forbidden" (-> anom :category)))
                 (is (= "invalid-password" (-> anom :reason)))))
             (done)))))

(deftest delete
  (async done
         (p/let [query (inline "mutations/user/delete.graphql")
                 ^js res (send
                          {:query query
                           :variables {:email "test@test.test"}})]
           (testing "returns data"
             (is (some? (-> res :data :delete))))
           (done))))
