(ns tests.core
  (:require
   [date-fns :as d]
   [cljs.test :as t :refer-macros [deftest async testing is use-fixtures]]
   [promesa.core :as p]
   [common.utils.date :refer (from-datetime-local)]
   [common.utils.promise :refer (each)]
   [ui.utils.error :refer (tr-error)]
   [ui.utils.i18n :refer (tr)]
   [tests.util.ui :as ui :refer (with-mounted-component test-app)]
   [tests.util.location :refer (generate-polyline)]
   [tests.user :as user]
   [tests.seat :as seat]
   [tests.address :as address]
   [tests.route :as route]
   [tests.location :as location]
   [tests.stop :as stop]))

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
               (-> (.findByText component (tr [:view.route.list/title]))
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
               (-> (.findByText component (tr [:view.route.list/title]))
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
                  (= "incorrect" (-> anom :category))
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
          (p/all (map (fn [_] (seat/create-seat)) (range 50)))
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
         (p/let [fetch-mock (seat/fetch-seats)]

           (testing "api returns data"
             (is (-> fetch-mock :result :data :seats first :id)))

           (with-mounted-component
             [test-app {:route "/seats" :mocks [fetch-mock]}]
             (fn [^js component]
               (-> (p/all (map #(.findByText component (:name %)) (-> fetch-mock :result :data :seats)))
                   (.then #(testing "ui presents seat names" (is (every? some? %))))
                   (.then done)))))))

(deftest create-addresses
  (async done
         (-> (p/all (map (fn [_] (address/create-address)) (range 50)))
             (.then (fn [mocks]
                      (testing "api returns data"
                        (is (every? #(-> % :result :data :createAddress) mocks)))

                      (p/let [create-mock (last mocks)
                              fetch-mock (address/fetch-addresses)]
                        (address/with-submit-address
                          {:mocks [create-mock fetch-mock]}
                          (fn [^js component]
                            (-> (.findByText component (-> create-mock :request :variables :name))
                                (.then #(testing "ui presents address name" (is (some? %))))
                                (.then done))))))))))

(deftest fetch-addresses
  (async done
         (p/let [fetch-mock (address/fetch-addresses)]
           (testing "api returns data"
             (is (-> fetch-mock :result :data :addresses first :id))

             (with-mounted-component
               [test-app {:route "/addresses" :mocks [fetch-mock]}]
               (fn [^js component]
                 (-> (p/all (map #(.findByText component (:name %)) (-> fetch-mock :result :data :addresses)))
                     (.then #(testing "ui presents address names" (is (every? some? %))))
                     (.then done))))))))

(deftest create-route
  (async done
         (p/let [fetch-user-mock (user/logged-in-user)
                 {:keys [seats addresses]} (-> fetch-user-mock :result :data :user)
                 promise-fn (fn [idx seat]
                              (let [shuffled-addresses (->> addresses shuffle (take (+ 2 (rand-int 8))))]
                                (fn []
                                  (route/create-route
                                   {:seatId (:id seat)
                                    :startAt (from-datetime-local (d/addDays (js/Date.) idx))
                                    :addressIds (mapv :id shuffled-addresses)
                                    :route {:legs []
                                            :path (->> shuffled-addresses
                                                       (map #(select-keys % [:lat :lng]))
                                                       (generate-polyline))
                                            :bounds {:north nil :east nil :south nil :west nil}}}))))
                 promise-fns (flatten
                              (map
                               (fn [idx]
                                 (map
                                  (fn [seat]
                                    (promise-fn idx seat))
                                  (take 10 seats)))
                               (range 3)))
                 create-mocks (each promise-fns)
                 create-mock (first create-mocks)
                 seat-id (-> create-mock :request :variables :seatId)
                 seat-name (->> seats (filter #(= seat-id (:id %))) first :name)
                 fetch-mock (route/fetch-routes
                             {:filters
                              {:start (-> (js/Date.) d/startOfDay)
                               :end (-> (js/Date.) d/endOfDay)
                               :status nil}})]

           (testing "api returns data"
             (is (every? #(-> % :result :data :createRoute) create-mocks)))

           (route/with-submit-route
             {:mocks [fetch-user-mock create-mock fetch-mock]}
             (fn [^js component]
               (-> (.findByText component seat-name)
                   (.then #(testing "ui presents seat name" (is (some? %))))
                   (.then done)))))))

(deftest fetch-routes
  (async done
         (p/let [fetch-mock (route/fetch-routes
                             {:filters
                              {:start (-> (js/Date.) d/startOfDay)
                               :end (-> (js/Date.) d/endOfDay)
                               :status nil}})
                 routes (-> fetch-mock :result :data :routes)]
           (testing "api returns data"
             (is (-> routes first :id))

             (with-mounted-component
               [test-app {:route "/routes" :mocks [fetch-mock]}]
               (fn [^js component]
                 (-> (p/all (map #(.findByText component (-> % :seat :name)) routes))
                     (.then #(testing "ui presents seat names" (is (every? some? %))))
                     (.then done))))))))

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

(deftest create-stop-arrival
  (async done
         (p/let [route-mocks (route/fetch-routes)
                 create-mocks (p/all (map
                                      (fn [{:keys [stops]}]
                                        (stop/create-stop-arrival (-> stops first :id)))
                                      (-> route-mocks :result :data :routes)))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createStopArrival :arrivedAt) create-mocks))
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
