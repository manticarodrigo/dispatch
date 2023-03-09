(ns tests.core
  (:require ["@faker-js/faker" :refer (faker)]
            [date-fns :as d]
            [cljs.test :as t :refer-macros [deftest async testing is use-fixtures]]
            [promesa.core :as p]
            [common.utils.promise :refer (each)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.i18n :refer (tr)]
            [tests.fixtures.tour :refer (depot shipments vehicles)]
            [tests.util.ui :as ui :refer (with-mounted-component test-app)]
            [tests.util.location :refer (nearby generate-polyline)]
            [tests.user :as user]
            [tests.agent :as agent]
            [tests.place :as place]
            [tests.task :as task]
            [tests.shipment :as shipment]
            [tests.plan :as plan]
            [tests.vehicle :as vehicle]
            [tests.location :as location]
            [tests.stop :as stop]))

(defmethod t/report [:cljs.test/default :begin-test-var] [m]
  (println "\n ◯" (-> m :var meta :name)))

(defmethod t/report [:cljs.test/default :pass] [m]
  (println "   *" (t/testing-contexts-str) "(PASS)"))

(use-fixtures :each
  {:before ui/before
   :after ui/after})

(def org-email "rodrigo@ambito.app")

(deftest register-success
  (async done
         (p/let [create-mock (user/register
                              {:email org-email
                               :organization "Test LLC"})]

           (testing "api returns data"
             (is (-> create-mock :result :data :register)))

           (user/with-submit-register
             {:mocks [create-mock]}
             (fn [^js component]
               (-> (.findByText component (tr [:view.login-confirm/title]))
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest register-conflict
  (async done
         (p/let [create-mock (user/register
                              {:email org-email
                               :organization "Test LLC"})
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
         (p/let [create-mock (user/login {:email org-email})]

           (testing "api returns data"
             (is (-> create-mock :result :data :login)))

           (user/with-submit-login
             {:mocks [create-mock]}
             (fn [^js component]
               (-> (.findByText component (tr [:view.login-confirm/title]))
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest login-invalid-email
  (async done
         (p/let [create-mock (user/login {:email "not@found.test"})
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

(deftest login-confirm
  (async done
         (p/let [login-mock (user/login {:email org-email})
                 confirm-mock (user/login-confirm {:code (-> login-mock :result :data :login js/parseInt)})]

           (testing "api returns data"
             (is (-> confirm-mock :result :data :loginConfirm)))

           (user/with-submit-confirm
             {:mocks [confirm-mock]}
             (fn [^js component]
               (-> (.findByText component (tr [:view.login/title]))
                   (.then #(testing "ui submits and redirects" (is (some? %))))
                   (.then done)))))))

(deftest scope-loader
  (async done
         (p/let [scope-mock (user/scope)]

           (testing "api returns data"
             (is (-> scope-mock :result :data :user :scope)))

           (user/with-scope-loader
             {:mocks [scope-mock]}
             (fn [^js component]
               (-> (.findByText component (tr [:view.task.list/title]))
                   (.then #(testing "ui redirects" (is (some? %))))
                   (.then done)))))))

(deftest create-agents
  (async done
         (->
          (p/all (map (fn [_] (agent/create-agent)) (range 12)))
          (.then (fn [mocks]
                   (testing "api returns data"
                     (is (every? #(-> % :result :data :createAgent) mocks)))

                   (p/let [create-mock (last mocks)
                           fetch-mock (agent/fetch-organization-agents)]
                     (agent/with-submit
                       {:mocks [create-mock fetch-mock]}
                       (fn [^js component]
                         (-> (.findByText component (-> create-mock :request :variables :name))
                             (.then #(testing "ui presents agent name" (is (some? %))))
                             (.then done))))))))))

(deftest fetch-agents
  (async done
         (p/let [fetch-mock (agent/fetch-organization-agents)
                 agents (-> fetch-mock :result :data :user :organization :agents)]

           (testing "api returns data"
             (is (-> agents first :id)))

           (with-mounted-component
             [test-app {:route "/organization/agents" :mocks [fetch-mock]}]
             (fn [^js component]
               (-> (p/all (map #(.findByText component (:name %)) agents))
                   (.then #(testing "ui presents agent names" (is (every? some? %))))
                   (.then done)))))))

(deftest create-places
  (async done
         (let [unique-names (->> (range 10)
                                 (map (fn [_] (.. faker -company name)))
                                 distinct)]
           (-> (p/all (map
                       (fn [name]
                         (let [[lat lng] (nearby)]
                           (place/create-place {:name name
                                                :description (.. faker -address (streetAddress true))
                                                :lat lat
                                                :lng lng})))
                       unique-names))
               (.then (fn [mocks]
                        (testing "api returns data"
                          (is (every? #(-> % :result :data :createPlace) mocks)))

                        (p/let [create-mock (last mocks)
                                fetch-mock (place/fetch-organization-places)]
                          (place/with-submit
                            {:mocks [create-mock fetch-mock]}
                            (fn [^js component]
                              (-> (.findByText component (-> create-mock :request :variables :name))
                                  (.then #(testing "ui presents place name" (is (some? %))))
                                  (.then done)))))))))))

(deftest fetch-places
  (async done
         (p/let [fetch-mock (place/fetch-organization-places)
                 places (-> fetch-mock :result :data :user :organization :places)]
           (testing "api returns data" (is (-> places first :id)))

           (with-mounted-component
             [test-app {:route "/organization/places" :mocks [fetch-mock]}]
             (fn [^js component]
               (-> (p/all (map #(.findByText component (:name %)) places))
                   (.then #(testing "ui presents place names" (is (every? some? %))))
                   (.then done)))))))

(deftest create-tasks
  (async done
         (p/let [fetch-options-mock (task/fetch-organization-task-options)
                 {:keys [agents places]} (-> fetch-options-mock :result :data :user :organization)
                 promise-fn (fn [idx agent]
                              (let [shuffled-places (->> places shuffle (take (+ 2 (rand-int 8))))]
                                (fn []
                                  (task/create-task
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
                 fetch-mock (task/fetch-organization-tasks
                             {:filters
                              {:start (-> (js/Date.) d/startOfDay)
                               :end (-> (js/Date.) d/endOfDay)
                               :status nil}})]

           (testing "api returns data"
             (is (every? #(-> % :result :data :createTask) create-mocks)))

           (task/with-submit
             {:mocks [fetch-options-mock create-mock fetch-mock]}
             (fn [^js component]
               (-> (.findByText component agent-name)
                   (.then #(testing "ui presents agent name" (is (some? %))))
                   (.then done)))))))

(deftest fetch-tasks
  (async done
         (p/let [fetch-mock (task/fetch-organization-tasks
                             {:filters
                              {:start (-> (js/Date.) d/startOfDay)
                               :end (-> (js/Date.) d/endOfDay)
                               :status nil}})
                 tasks (-> fetch-mock :result :data :user :organization :tasks)]
           (testing "api returns data" (is (-> tasks first :id)))

           (with-mounted-component
             [test-app {:route "/organization/tasks" :mocks [fetch-mock]}]
             (fn [^js component]
               (-> (p/all (map #(.findByText component (-> % :agent :name)) tasks))
                   (.then #(testing "ui presents agent names" (is (every? some? %))))
                   (.then done)))))))

(deftest fetch-tasks-for-agent
  (async done
         (p/let [agents-mock (agent/fetch-organization-agents)
                 agent-id (-> agents-mock :result :data :user :organization :agents first :id)
                 {:keys [result]} (agent/fetch-organization-agent {:agentId agent-id})]
           (testing "api returns data"
             (is (-> result :data :user :organization :agent :tasks first :id))
             (done)))))

(deftest create-arrivals
  (async done
         (p/let [fetch-mocks (task/fetch-organization-tasks)
                 create-mocks (p/all (map
                                      (fn [{:keys [stops]}]
                                        (stop/create-arrival (-> stops first :id)))
                                      (-> fetch-mocks :result :data :user :organization :tasks)))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createArrival :arrivedAt) create-mocks))
             (done)))))

(deftest create-demo-shipments
  (async done
         (-> (p/all (map
                     (fn [{:keys [reference address size duration windows latitude longitude]}]
                       (p/let [place-mock (place/create-place
                                           {:name reference
                                            :description address
                                            :lat latitude
                                            :lng longitude})]
                         (shipment/create-shipment
                          {:placeId (-> place-mock :result :data :createPlace :id)
                           :size size
                           :duration duration
                           :windows windows})))
                     shipments))
             (.then (fn [mocks]
                      (testing "api returns data"
                        (is (every? #(-> % :result :data :createShipment) mocks))
                        (done)))))))

(deftest fetch-demo-shipments
  (async done
         (p/let [fetch-mock (shipment/fetch-organization-shipments)]
           (testing "api returns data"
             (is (-> fetch-mock :result :data :user :organization :shipments first :id))
             (done)))))

(deftest create-demo-vehicles
  (async done
         (-> (p/all (map vehicle/create-vehicle vehicles))
             (.then (fn [mocks]
                      (testing "api returns data"
                        (is (every? #(-> % :result :data :createVehicle) mocks))
                        (done)))))))

(deftest fetch-demo-vehicles
  (async done
         (p/let [fetch-mock (vehicle/fetch-organization-vehicles)]
           (testing "api returns data"
             (is (-> fetch-mock :result :data :user :organization :vehicles first :id))
             (done)))))

(deftest create-demo-plan
  (async done
         (p/let [shipments-mock (shipment/fetch-organization-shipments)
                 vehicles-mock (vehicle/fetch-organization-vehicles)
                 shipments (-> shipments-mock :result :data :user :organization :shipments)
                 vehicles (-> vehicles-mock :result :data :user :organization :vehicles)
                 depot-place (place/create-place (:place depot))
                 plan-mock (plan/create-plan
                            {:depotId (-> depot-place :result :data :createPlace :id)
                             :startAt (-> depot :startAt)
                             :endAt (-> depot :endAt)
                             :breaks (-> depot :breaks)
                             :shipmentIds (mapv :id shipments)
                             :vehicleIds (mapv :id vehicles)})]
           (testing "api returns data"
             (is (-> plan-mock :result :data :createPlan))
             (done)))))

(deftest create-plan-tasks
  (async done
         (p/let [plans-mock (plan/fetch-organization-plans)
                 plans (-> plans-mock :result :data :user :organization :plans)
                 plan (first plans)
                 {:keys [shipments vehicles]} plan
                 agents-mock (agent/fetch-organization-agents)
                 agents (-> agents-mock :result :data :user :organization :agents)
                 shipment-id-chunks (partition
                                     (int (/ (count shipments)
                                             (count vehicles)))
                                     (->> shipments (map :id)))
                 create-mock (plan/create-plan-tasks
                              {:input {:planId (:id plan)
                                       :assignments (map-indexed
                                                     (fn [idx vehicle]
                                                       {:agentId (-> agents (nth idx) :id)
                                                        :vehicleId (:id vehicle)
                                                        :visits (map
                                                                 #(hash-map :shipmentId %)
                                                                 (nth shipment-id-chunks idx))})
                                                     vehicles)}})]
           (testing "api returns data"
             (is (-> create-mock :result :data :createPlanTasks))
             (done)))))

(deftest create-agent-locations
  (async done
         (p/let [agents-mock (agent/fetch-organization-agents)
                 agents (->> agents-mock :result :data :user :organization :agents (drop 1))
                 create-mocks (each (map-indexed
                                     (fn [idx agent]
                                       (let [phone (-> agent :user :phone)
                                             created-at (-> (js/Date.) (d/subHours (* idx 10)))]
                                         (fn []
                                           (p/do
                                             (user/with-phone-login phone)
                                             (location/create created-at)))))
                                     agents))]
           (testing "api returns data"
             (is (every? #(-> % :result :data :createLocation) create-mocks))
             (done)))))
