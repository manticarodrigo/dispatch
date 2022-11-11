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

(deftest not-found
  (async done
         (with-mounted-component
           [test-app {:route "/not-found"
                      :mocks []}]
           (fn [^js component]
             (p/let [el (.getByText component "Page not found.")]
               (testing "is not found message in paragraph tag"
                 (is (= "P" (. el -tagName))))
               (done))))))

(deftest register
  (async done
         (let [query (inline "mutations/user/register.graphql")
               payload  {:query query
                         :variables {:firstName "test"
                                     :lastName "test"
                                     :email "test@test.test"
                                     :password "test"}}]
           (p/do
             (p/let [res (send payload)]
               (testing "returns data"
                 (is (some-> res :data :register)))
               (with-mounted-component
                 [test-app {:route "/register"
                            :mocks [{:request payload :result res}]}]
                 (fn [^js component]
                   (p/let [form (-> component (.-container) (.querySelector "form"))
                           first-name-input (.getByLabelText component "First name")
                           last-name-input (.getByLabelText component "Last name")
                           email-input (.getByLabelText component "Email")
                           password-input (.getByLabelText component "Password")]
                     (p/do
                       (change first-name-input "test")
                       (change last-name-input "test")
                       (change email-input "test@test.test")
                       (change password-input "test")
                       (submit form)
                       (-> (.findByText component "Fleet")
                           (.then #(testing "form submits and redirects"
                                     (is (some? %))))))))))
             (p/let [res (send payload)
                     error (-> res :errors first)
                     anom (-> error :extensions :anom)]
               (testing "returns unique email anom"
                 (is (= "conflict" (-> anom :category)))
                 (is (= "unique-constraint" (-> anom :reason)))
                 (is (= "email" (-> anom :meta :target first)))))
             (done)))))

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
