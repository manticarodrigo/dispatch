(ns tests.core
  (:require
   [shadow.resource :refer (inline)]
   [cljs.test :as t :refer-macros [deftest async testing is use-fixtures]]
   [promesa.core :as p]
   [tests.util.api :refer (send)]
   [tests.util.ui :as ui :refer (with-mounted-component test-app change submit)]
   [ui.utils.error :refer (tr-error)]))

(defmethod t/report [:cljs.test/default :begin-test-var] [m]
  (println " ◯" (-> m :var meta :name)))

(defmethod t/report [:cljs.test/default :pass] [m]
  (println "   *" (t/testing-contexts-str) "(PASS)"))

(use-fixtures :each
  {:before ui/before
   :after ui/after})

(deftest register-success
  (async done
         (p/let [query (inline "mutations/user/create.graphql")
                 variables {:email "test@test.test" :password "test"}
                 request  {:query query :variables variables}
                 result (send request)]

           (testing "api returns data"
             (is (some-> result :data :createUser)))

           (with-mounted-component
             [test-app {:route "/register" :mocks [{:request request :result result}]}]
             (fn [^js component]
               (p/do
                 (change (.getByLabelText component "Email") (:email variables))
                 (change (.getByLabelText component "Password") (:password variables))
                 (submit (-> component (.-container) (.querySelector "form")))
                 (-> (.findByText component "Fleet")
                     (.then #(testing "submits and redirects" (is (some? %))))
                     (.then done))))))))

(deftest register-conflict
  (async done
         (p/let [query (inline "mutations/user/create.graphql")
                 variables {:email "test@test.test" :password "test"}
                 request  {:query query :variables variables}
                 result (send request)
                 error (-> result :errors first)
                 anom (-> error :extensions :anom)]

           (testing "api returns anom"
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
                 (-> (.findByText component (tr-error anom))
                     (.then #(testing "presents unique email anom" (is (some? %))))
                     (.then done))))))))

(deftest login-success
  (async done
         (p/let [query (inline "mutations/user/login.graphql")
                 variables {:email "test@test.test" :password "test"}
                 request {:query query :variables variables}
                 result (send request)]

           (testing "api returns data"
             (is (some-> result :data :loginUser)))

           (with-mounted-component
             [test-app {:route "/login" :mocks [{:request request :result result}]}]
             (fn [^js component]
               (p/do
                 (change (.getByLabelText component "Email") (:email variables))
                 (change (.getByLabelText component "Password") (:password variables))
                 (submit (-> component (.-container) (.querySelector "form")))
                 (-> (.findByText component "Fleet")
                     (.then #(testing "submits and redirects" (is (some? %))))
                     (.then done))))))))

(deftest login-invalid-email
  (async done
         (p/let [query (inline "mutations/user/login.graphql")
                 variables {:email "not@found.test" :password "test"}
                 request {:query query :variables variables}
                 result (send request)
                 anom (-> result :errors first :extensions :anom)]

           (testing "api returns anom"
             (is (and
                  (= "not-found" (-> anom :category))
                  (= "account-not-found" (-> anom :reason)))))

           (with-mounted-component
             [test-app {:route "/login" :mocks [{:request request :result result}]}]
             (fn [^js component]
               (p/do
                 (change (.getByLabelText component "Email") (:email variables))
                 (change (.getByLabelText component "Password") (:password variables))
                 (submit (-> component (.-container) (.querySelector "form")))
                 (-> (.findByText component (tr-error anom))
                     (.then #(testing "presents account not found anom" (is (some? %))))
                     (.then done))))))))

(deftest login-invalid-password
  (async done
         (p/let [query (inline "mutations/user/login.graphql")
                 variables {:email "test@test.test" :password "incorrect"}
                 request {:query query :variables variables}
                 result (send request)
                 anom (-> result :errors first :extensions :anom)]

           (testing "api returns anom"
             (is (and
                  (= "forbidden" (-> anom :category))
                  (= "invalid-password" (-> anom :reason)))))

           (with-mounted-component
             [test-app {:route "/login" :mocks [{:request request :result result}]}]
             (fn [^js component]
               (p/do
                 (change (.getByLabelText component "Email") (:email variables))
                 (change (.getByLabelText component "Password") (:password variables))
                 (submit (-> component (.-container) (.querySelector "form")))
                 (-> (.findByText component (tr-error anom))
                     (.then #(testing "presents invalid password anom" (is (some? %))))
                     (.then done))))))))

(deftest find-user
  (async done
         (p/let [query (inline "queries/user/find.graphql")
                 variables {:email "test@test.test"}
                 request  {:query query :variables variables}
                 result (send request)]
           (testing "api returns data"
             (is (some-> result :data :findUser :id)))
           (done))))
