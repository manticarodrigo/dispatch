(ns tests.core
  (:require
   [date-fns :as d]
   [shadow.resource :refer (inline)]
   [cljs.test :as t :refer-macros [deftest async testing is use-fixtures]]
   [promesa.core :as p]
   [common.utils.date :refer (from-datetime-local)]
   [ui.utils.error :refer (tr-error)]
   [tests.util.api :refer (send)]
   [tests.util.ui :as ui :refer (with-mounted-component test-app)]
   [tests.user :as user]
   [tests.seat :as seat]
   [tests.address :as address]
   [tests.route :as route]
   [tests.location :as location]))

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
         (->
          (p/all (map (fn [_] (seat/create-seat)) (range 5)))
          (.then (fn [mocks]
                   (testing "api returns data"
                     (is (every? #(-> % :result :data :createSeat) mocks)))

                   (p/let [create-mock (last mocks)
                           fetch-mock (seat/fetch-seats)]
                     (seat/with-submit-seat
                       {:mocks [create-mock fetch-mock]}
                       (fn [^js component]
                         (-> (.findByText component (-> create-mock :request :variables :name))
                             (.then #(testing "ui presents seat name" (is (some? %))))
                             (.then done))))))))))

(deftest fetch-seats
  (async done
         (p/let [{:keys [request result]} (seat/fetch-seats)]

           (testing "api returns data"
             (is (-> result :data :seats first :id)))

           (with-mounted-component
             [test-app {:route "/fleet" :mocks [{:request request :result result}]}]
             (fn [^js component]
               (p/->
                (p/all
                 (map (fn [{:keys [name]}]
                        (-> (.findByText component name)
                            (.then #(testing "ui presents seat name" (is (some? %))))))
                      (-> result :data :seats)))
                done))))))

(deftest create-addresses
  (async done
         (-> (p/all (map (fn [_] (address/create-address)) (range 5)))
             (.then (fn [mocks]
                      (testing "api returns data"
                        (is (every? #(-> % :result :data :createAddress) mocks)))

                      (p/let [create-mock (last mocks)]
                        (address/with-submit-address
                          {:mocks [create-mock]}
                          (fn [^js component]
                            (-> (.findByText component "Route")
                                (.then #(testing "ui submits and redirects" (is (some? %))))
                                (.then done))))))))))

(deftest fetch-addresses
  (async done
         (p/let [{:keys [result]} (address/fetch-addresses)]
           (testing "api returns data"
             (is (-> result :data :addresses first :id))
             (done)))))

(deftest create-route
  (async done
         (p/let [fetch-mock (user/logged-in-user)
                 seats (-> fetch-mock :result :data :user :seats)
                 create-mocks (p/all
                               (flatten
                                (map
                                 (fn [idx]
                                   (map
                                    (fn [seat]
                                      (route/create-route
                                       {:seatId (:id seat)
                                        :startAt (from-datetime-local (d/addDays (js/Date.) idx))
                                        :addressIds (mapv :id (-> fetch-mock
                                                                  :result
                                                                  :data
                                                                  :user
                                                                  :addresses))}))
                                    seats))
                                 (range 5))))]

           (testing "api returns data"
             (is (every? #(-> % :result :data :createRoute) create-mocks)))

           (route/with-submit-route
             {:mocks [fetch-mock (first create-mocks)]}
             (fn [^js component]
               (-> (.findByText component "Fleet")
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest fetch-routes
  (async done
         (p/let [query (inline "queries/route/fetch-all.graphql")
                 request  {:query query}
                 result (send request)]
           (testing "api returns data"
             (is (-> result :data :routes first :id))
             (done)))))

(deftest create-location
  (async done
         (p/let [seats-mock (seat/fetch-seats)
                 seat-ids (->> seats-mock :result :data :seats (map :id) (drop 1))
                 create-mocks (p/all (map-indexed
                                      (fn [idx seat-id]
                                        (let [created-at (-> (js/Date.)
                                                             (d/subHours (* idx 10))
                                                             from-datetime-local)]
                                          (location/create-location seat-id created-at)))
                                      seat-ids))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createLocation) create-mocks))
             (done)))))

(deftest fetch-seat
  (async done
         (p/let [seats-mock (seat/fetch-seats)
                 id (-> seats-mock :result :data :seats first :id)
                 {:keys [result]} (seat/fetch-seat {:id id})]
           (testing "api returns data"
             (is (-> result :data :seat :routes first :id))
             (done)))))

(deftest logged-in-user
  (async done
         (p/let [{:keys [result]} (user/logged-in-user)]
           (testing "api returns data"
             (is (-> result :data :user :id)))
           (done))))
