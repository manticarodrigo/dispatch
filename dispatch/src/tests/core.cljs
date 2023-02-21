(ns tests.core
  (:require ["@faker-js/faker" :refer (faker)]
            [date-fns :as d]
            [cljs.test :as t :refer-macros [deftest async testing is use-fixtures]]
            [promesa.core :as p]
            [common.utils.promise :refer (each)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.i18n :refer (tr)]
            [tests.util.ui :as ui :refer (with-mounted-component test-app)]
            [tests.util.location :refer (nearby generate-polyline)]
            [tests.user :as user]
            [tests.agent :as agent]
            [tests.device :as device]
            [tests.location :as location]
            [tests.place :as place]
            [tests.task :as task]
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
         (p/let [create-mock (user/register
                              {:email "test@test.test"
                               :password "test"
                               :organization "test"})]

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
                               :password "test"
                               :organization "test"})
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

(deftest create-agents
  (async done
         (->
          (p/all (map (fn [_] (agent/create)) (range 50)))
          (.then (fn [mocks]
                   (testing "api returns data"
                     (is (every? #(-> % :result :data :createAgent) mocks)))

                   (p/let [create-mock (last mocks)
                           fetch-mock (agent/find-all)]
                     (agent/with-submit
                       {:mocks [create-mock fetch-mock]}
                       (fn [^js component]
                         (-> (.findByText component (-> create-mock :request :variables :name))
                             (.then #(testing "ui presents agent name" (is (some? %))))
                             (.then done))))))))))

(deftest fetch-agents
  (async done
         (p/let [fetch-mock (agent/find-all)
                 agents (-> fetch-mock :result :data :agents)]

           (testing "api returns data"
             (is (-> agents first :id)))

           (with-mounted-component
             [test-app {:route "/admin/agents" :mocks [fetch-mock]}]
             (fn [^js component]
               (-> (p/all (map #(.findByText component (:name %)) agents))
                   (.then #(testing "ui presents agent names" (is (every? some? %))))
                   (.then done)))))))

(deftest create-devices
  (async done
         (p/let [agents-mock (agent/find-all)
                 agent-ids (->> agents-mock :result :data :agents (map :id))
                 create-mocks (p/all (map-indexed
                                      (fn [idx agent-id]
                                        (device/create
                                         {:agentId agent-id
                                          :deviceId idx
                                          :info {}}))
                                      agent-ids))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createDevice) create-mocks))
             (done)))))

(deftest create-locations
  (async done
         (p/let [agents-mock (agent/find-all)
                 agents (->> agents-mock :result :data :agents (drop 1))
                 create-mocks (p/all (map-indexed
                                      (fn [idx {:keys [id device]}]
                                        (let [created-at (-> (js/Date.) (d/subHours (* idx 10)))]
                                          (location/create id (:id device) created-at)))
                                      agents))]
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
                 {:keys [agents places]} (-> fetch-user-mock :result :data :user)
                 promise-fn (fn [idx agent]
                              (let [shuffled-places (->> places shuffle (take (+ 2 (rand-int 8))))]
                                (fn []
                                  (task/create
                                   {:agentId (:id agent)
                                    :startAt (-> (js/Date.)
                                                 (d/set
                                                  #js{:hours 8
                                                      :minutes 0
                                                      :seconds 0
                                                      :milliseconds 0})
                                                 (d/addDays idx))
                                    :placeIds (mapv :id shuffled-places)
                                    :route {:legs (->> shuffled-places
                                                       (map-indexed
                                                        (fn [idx place]
                                                          {:duration (if (> idx 0) 1800 0)
                                                           :distance (if (> idx 0) 1000 0)
                                                           :address (:description place)
                                                           :location (select-keys place [:lat :lng])})))
                                            :path (->> shuffled-places
                                                       (map #(select-keys % [:lat :lng]))
                                                       (generate-polyline))
                                            :bounds {:north nil :east nil :south nil :west nil}}}))))
                 promise-fns (flatten
                              (map
                               (fn [idx]
                                 (map
                                  (fn [agent]
                                    (promise-fn idx agent))
                                  (take 10 agents)))
                               (range 3)))
                 create-mocks (each promise-fns)
                 create-mock (first create-mocks)
                 agent-id (-> create-mock :request :variables :agentId)
                 agent-name (->> agents (filter #(= agent-id (:id %))) first :name)
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
               (-> (.findByText component agent-name)
                   (.then #(testing "ui presents agent name" (is (some? %))))
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
               (-> (p/all (map #(.findByText component (-> % :agent :name)) tasks))
                   (.then #(testing "ui presents agent names" (is (every? some? %))))
                   (.then done)))))))


(deftest fetch-tasks-for-agent
  (async done
         (p/let [agents-mock (agent/find-all)
                 agent-id (-> agents-mock :result :data :agents first :id)
                 {:keys [result]} (agent/find-unique {:agentId agent-id})]
           (testing "api returns data"
             (is (-> result :data :agent :tasks first :id))
             (done)))))

(deftest create-arrivals
  (async done
         (p/let [fetch-mocks (task/find-all)
                 create-mocks (p/all (map
                                      (fn [{:keys [stops]}]
                                        (stop/create-arrival (-> stops first :id)))
                                      (-> fetch-mocks :result :data :tasks)))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createArrival :arrivedAt) create-mocks))
             (done)))))
