(ns tests.core
  (:require ["@faker-js/faker" :refer (faker)]
            [date-fns :as d]
            [cljs.test :as t :refer-macros [deftest async testing is use-fixtures]]
            [promesa.core :as p]
            [common.utils.date :refer (from-datetime-local)]
            [common.utils.promise :refer (each)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.i18n :refer (tr)]
            [tests.util.ui :as ui :refer (with-mounted-component test-app)]
            [tests.util.location :refer (nearby generate-polyline)]
            [tests.user :as user]
            [tests.seat :as seat]
            [tests.device :as device]
            [tests.location :as location]
            [tests.place :as place]
            [tests.task :as task]
            [tests.waypoint :as waypoint]))

(defmethod t/report [:cljs.test/default :begin-test-var] [m]
  (println "\n ◯" (-> m :var meta :name)))

(defmethod t/report [:cljs.test/default :pass] [m]
  (println "   *" (t/testing-contexts-str) "(PASS)"))

(use-fixtures :each
  {:before ui/before
   :after ui/after})

(deftest register-success
  (async done
         (p/let [create-mock (user/register
                              {:email "test@test.test"
                               :password "test"})]

           (testing "api returns data"
             (is (-> create-mock :result :data :createUser)))

           (user/with-submit-register
             {:mocks [create-mock]}
             (fn [^js component]
               (-> (.findByText component (tr [:view.task.list/title]))
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest register-conflict
  (async done
         (p/let [create-mock (user/register
                              {:email "test@test.test"
                               :password "test"})
                 error (-> create-mock :result :errors first)
                 anom (-> error :extensions :anom)]

           (testing "api returns anom"
             (is (and
                  (= "conflict" (-> anom :category))
                  (= "unique-constraint" (-> anom :reason))
                  (= "email" (-> anom :meta :target first)))))

           (user/with-submit-register
             {:mocks [create-mock]}
             (fn [^js component]
               (-> (.findByText component (tr-error anom))
                   (.then #(testing "ui presents unique email anom" (is (some? %))))
                   (.then done)))))))

(deftest login-success
  (async done
         (p/let [create-mock (user/login
                              {:email "test@test.test"
                               :password "test"})]

           (testing "api returns data"
             (is (-> create-mock :result :data :createSession)))

           (user/with-submit-login
             {:mocks [create-mock]}
             (fn [^js component]
               (-> (.findByText component (tr [:view.task.list/title]))
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest login-invalid-email
  (async done
         (p/let [create-mock (user/login
                              {:email "not@found.test"
                               :password "test"})
                 anom (-> create-mock :result :errors first :extensions :anom)]

           (testing "api returns anom"
             (is (and
                  (= "not-found" (-> anom :category))
                  (= "account-not-found" (-> anom :reason)))))

           (user/with-submit-login
             {:mocks [create-mock]}
             (fn [^js component]
               (-> (.findByText component (tr-error anom))
                   (.then #(testing "ui presents account not found anom" (is (some? %))))
                   (.then done)))))))

(deftest login-invalid-password
  (async done
         (p/let [create-mock (user/login
                              {:email "test@test.test"
                               :password "incorrect"})
                 anom (-> create-mock :result :errors first :extensions :anom)]

           (testing "api returns anom"
             (is (and
                  (= "incorrect" (-> anom :category))
                  (= "invalid-password" (-> anom :reason)))))

           (user/with-submit-login
             {:mocks [create-mock]}
             (fn [^js component]
               (-> (.findByText component (tr-error anom))
                   (.then #(testing "ui presents invalid password anom" (is (some? %))))
                   (.then done)))))))

(deftest fetch-active-user
  (async done
         (p/let [{:keys [result]} (user/active-user)]
           (testing "api returns data"
             (is (-> result :data :user :id)))
           (done))))

(deftest create-seats
  (async done
         (->
          (p/all (map (fn [_] (seat/create)) (range 50)))
          (.then (fn [mocks]
                   (testing "api returns data"
                     (is (every? #(-> % :result :data :createSeat) mocks)))

                   (p/let [create-mock (last mocks)
                           fetch-mock (seat/find-all)]
                     (seat/with-submit
                       {:mocks [create-mock fetch-mock]}
                       (fn [^js component]
                         (-> (.findByText component (-> create-mock :request :variables :name))
                             (.then #(testing "ui presents seat name" (is (some? %))))
                             (.then done))))))))))

(deftest fetch-seats
  (async done
         (p/let [fetch-mock (seat/find-all)
                 seats (-> fetch-mock :result :data :seats)]

           (testing "api returns data"
             (is (-> seats first :id)))

           (with-mounted-component
             [test-app {:route "/admin/seats" :mocks [fetch-mock]}]
             (fn [^js component]
               (-> (p/all (map #(.findByText component (:name %)) seats))
                   (.then #(testing "ui presents seat names" (is (every? some? %))))
                   (.then done)))))))

(deftest create-devices
  (async done
         (p/let [seats-mock (seat/find-all)
                 seat-ids (->> seats-mock :result :data :seats (map :id))
                 create-mocks (p/all (map-indexed
                                      (fn [idx seat-id]
                                        (device/create
                                         {:seatId seat-id
                                          :deviceId idx
                                          :info {}}))
                                      seat-ids))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createDevice) create-mocks))
             (done)))))

(deftest create-locations
  (async done
         (p/let [seats-mock (seat/find-all)
                 seats (->> seats-mock :result :data :seats (drop 1))
                 create-mocks (p/all (map-indexed
                                      (fn [idx {:keys [id device]}]
                                        (let [created-at (-> (js/Date.)
                                                             (d/subHours (* idx 10))
                                                             from-datetime-local)]
                                          (location/create id (:id device) created-at)))
                                      seats))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createLocation) create-mocks))
             (done)))))

(deftest create-places
  (async done
         (let [unique-names (->> (range 50)
                                 (map (fn [_] (.. faker -company name)))
                                 distinct)]
           (-> (p/all (map
                       (fn [name]
                         (let [[lat lng] (nearby)]
                           (place/create {:name name
                                          :description (.. faker -address (streetAddress true))
                                          :lat lat
                                          :lng lng})))
                       unique-names))
               (.then (fn [mocks]
                        (testing "api returns data"
                          (is (every? #(-> % :result :data :createPlace) mocks)))

                        (p/let [create-mock (last mocks)
                                fetch-mock (place/find-all)]
                          (place/with-submit
                            {:mocks [create-mock fetch-mock]}
                            (fn [^js component]
                              (-> (.findByText component (-> create-mock :request :variables :name))
                                  (.then #(testing "ui presents place name" (is (some? %))))
                                  (.then done)))))))))))

(deftest fetch-places
  (async done
         (p/let [fetch-mock (place/find-all)
                 places (-> fetch-mock :result :data :places)]
           (testing "api returns data" (is (-> places first :id)))

           (with-mounted-component
             [test-app {:route "/admin/places" :mocks [fetch-mock]}]
             (fn [^js component]
               (-> (p/all (map #(.findByText component (:name %)) places))
                   (.then #(testing "ui presents place names" (is (every? some? %))))
                   (.then done)))))))

(deftest create-tasks
  (async done
         (p/let [fetch-user-mock (user/active-user)
                 {:keys [seats places]} (-> fetch-user-mock :result :data :user)
                 promise-fn (fn [idx seat]
                              (let [shuffled-places (->> places shuffle (take (+ 2 (rand-int 8))))]
                                (fn []
                                  (task/create
                                   {:seatId (:id seat)
                                    :startAt (from-datetime-local (d/addDays (js/Date.) idx))
                                    :placeIds (mapv :id shuffled-places)
                                    :route {:legs []
                                            :path (->> shuffled-places
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
                 fetch-mock (task/find-all
                             {:filters
                              {:start (-> (js/Date.) d/startOfDay)
                               :end (-> (js/Date.) d/endOfDay)
                               :status nil}})]

           (testing "api returns data"
             (is (every? #(-> % :result :data :createTask) create-mocks)))

           (task/with-submit
             {:mocks [fetch-user-mock create-mock fetch-mock]}
             (fn [^js component]
               (-> (.findByText component seat-name)
                   (.then #(testing "ui presents seat name" (is (some? %))))
                   (.then done)))))))

(deftest fetch-tasks
  (async done
         (p/let [fetch-mock (task/find-all
                             {:filters
                              {:start (-> (js/Date.) d/startOfDay)
                               :end (-> (js/Date.) d/endOfDay)
                               :status nil}})
                 tasks (-> fetch-mock :result :data :tasks)]
           (testing "api returns data" (is (-> tasks first :id)))

           (with-mounted-component
             [test-app {:route "/admin/tasks" :mocks [fetch-mock]}]
             (fn [^js component]
               (-> (p/all (map #(.findByText component (-> % :seat :name)) tasks))
                   (.then #(testing "ui presents seat names" (is (every? some? %))))
                   (.then done)))))))


(deftest fetch-tasks-for-seat
  (async done
         (p/let [seats-mock (seat/find-all)
                 seat-id (-> seats-mock :result :data :seats first :id)
                 {:keys [result]} (seat/find-unique {:seatId seat-id})]
           (testing "api returns data"
             (is (-> result :data :seat :tasks first :id))
             (done)))))

(deftest create-waypoint-arrivals
  (async done
         (p/let [fetch-mocks (task/find-all)
                 create-mocks (p/all (map
                                      (fn [{:keys [waypoints]}]
                                        (waypoint/create-arrival (-> waypoints first :id)))
                                      (-> fetch-mocks :result :data :tasks)))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createArrival :arrivedAt) create-mocks))
             (done)))))
