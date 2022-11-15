(ns tests.core
  (:require
   [shadow.resource :refer (inline)]
   [cljs.test :as t :refer-macros [deftest async testing is use-fixtures]]
   [promesa.core :as p]
   [tests.util.api :refer (send)]
   [tests.util.ui :as ui :refer (with-mounted-component test-app)]
   [ui.utils.error :refer (tr-error)]
   [tests.user :as user]
   [tests.seat :as seat]
   [tests.address :as address]
   [tests.route :as route]))

(defmethod t/report [:cljs.test/default :begin-test-var] [m]
  (println "\n ◯" (-> m :var meta :name)))

(defmethod t/report [:cljs.test/default :pass] [m]
  (println "   *" (t/testing-contexts-str) "(PASS)"))

(use-fixtures :each
  {:before ui/before
   :after ui/after})

(deftest register-success
  (async done
         (p/let [{:keys [request result]} (user/register-user
                                           {:email "test@test.test"
                                            :password "test"})]

           (testing "api returns data"
             (is (-> result :data :createUser)))

           (user/with-submit-register
             {:mocks [{:request request :result result}]}
             (fn [^js component]
               (-> (.findByText component "Fleet")
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest register-conflict
  (async done
         (p/let [{:keys [request result]} (user/register-user
                                           {:email "test@test.test"
                                            :password "test"})
                 error (-> result :errors first)
                 anom (-> error :extensions :anom)]

           (testing "api returns anom"
             (is (and
                  (= "conflict" (-> anom :category))
                  (= "unique-constraint" (-> anom :reason))
                  (= "email" (-> anom :meta :target first)))))

           (user/with-submit-register
             {:mocks [{:request request :result result}]}
             (fn [^js component]
               (-> (.findByText component (tr-error anom))
                   (.then #(testing "ui presents unique email anom" (is (some? %))))
                   (.then done)))))))

(deftest login-success
  (async done
         (p/let [{:keys [request result]} (user/login-user
                                           {:email "test@test.test"
                                            :password "test"})]

           (testing "api returns data"
             (is (-> result :data :loginUser)))

           (user/with-submit-login
             {:mocks [{:request request :result result}]}
             (fn [^js component]
               (-> (.findByText component "Fleet")
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest login-invalid-email
  (async done
         (p/let [{:keys [request result]} (user/login-user
                                           {:email "not@found.test"
                                            :password "test"})
                 anom (-> result :errors first :extensions :anom)]

           (testing "api returns anom"
             (is (and
                  (= "not-found" (-> anom :category))
                  (= "account-not-found" (-> anom :reason)))))

           (user/with-submit-login
             {:mocks [{:request request :result result}]}
             (fn [^js component]
               (-> (.findByText component (tr-error anom))
                   (.then #(testing "ui presents account not found anom" (is (some? %))))
                   (.then done)))))))

(deftest login-invalid-password
  (async done
         (p/let [{:keys [request result]} (user/login-user
                                           {:email "test@test.test"
                                            :password "incorrect"})
                 anom (-> result :errors first :extensions :anom)]

           (testing "api returns anom"
             (is (and
                  (= "forbidden" (-> anom :category))
                  (= "invalid-password" (-> anom :reason)))))

           (user/with-submit-login
             {:mocks [{:request request :result result}]}
             (fn [^js component]
               (-> (.findByText component (tr-error anom))
                   (.then #(testing "ui presents invalid password anom" (is (some? %))))
                   (.then done)))))))

(deftest create-seats
  (async done
         (p/->
          (p/all
           (map (fn [_]
                  (p/let [{:keys [result]} (seat/create-seat)]
                    (testing "api returns data"
                      (is (-> result :data :createSeat)))))
                (range 3)))
          done)))

(deftest find-seats
  (async done
         (p/let [{:keys [request result]} (seat/find-seats)]

           (testing "api returns data"
             (is (-> result :data :seats first :id)))

           (with-mounted-component
             [test-app {:route "/fleet" :mocks [{:request request :result result}]}]
             (fn [^js component]
               (p/-> (p/all (map (fn [{:keys [name]}]
                                   (-> (.findByText component name)
                                       (.then #(testing "ui presents seat name" (is (some? %))))))
                                 (-> result :data :seats)))
                     done))))))

(deftest create-addresses
  (async done
         (p/->
          (p/all (map (fn [_]
                        (p/let [{:keys [result]} (address/create-address)]
                          (testing "api returns data"
                            (is (-> result :data :createAddress)))))
                      (range 3)))
          done)))

(deftest find-addresses
  (async done
         (p/let [{:keys [result]} (address/find-addresses)]
           (testing "api returns data"
             (is (-> result :data :addresses first :id))
             (done)))))

(deftest create-route
  (async done
         (p/let [user-res (user/logged-in-user)
                 {:keys [result] :as route-res} (route/create-route user-res)]

           (testing "api returns data"
             (is (-> result :data :createRoute)))

           (route/with-submit-route
             {:mocks [(select-keys user-res [:request :result])
                      (select-keys route-res [:request :result])]}
             (fn [^js component]
               (-> (.findByText component "Fleet")
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest find-routes
  (async done
         (p/let [query (inline "queries/route/fetch-all.graphql")
                 request  {:query query}
                 result (send request)]
           (testing "api returns data"
             (is (-> result :data :routes first :id))
             (done)))))

(deftest logged-in-user
  (async done
         (p/let [{:keys [result]} (user/logged-in-user)]
           (testing "api returns data"
             (is (-> result :data :user :id)))
           (done))))
