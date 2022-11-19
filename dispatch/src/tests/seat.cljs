(ns tests.seat
  (:require ["@faker-js/faker" :refer (faker)]
            [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component test-app change submit)]))

(defn create-seat []
  (p/let [query (inline "mutations/seat/create.graphql")
          variables {:name (.. faker -name fullName)}
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn fetch-seats []
  (p/let [query (inline "queries/seat/fetch-all.graphql")
          request  {:query query}
          result (send request)]
    {:request request
     :result result}))

(defn fetch-seat [variables]
  (p/let [query (inline "queries/seat/fetch.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn with-submit-seat [ctx f]
  (let [{:keys [mocks]} ctx
        [fetch-res create-res] mocks
        {:keys [request]} create-res
        {:keys [variables]} request]

    (with-mounted-component
      [test-app {:route "/fleet?modal=seat" :mocks mocks}]
      (fn [^js component]
        (p/do
          (.findByText component (-> fetch-res :result :data :seats first :name))
          (change (.getByLabelText component "Name") (:name variables))
          (submit (-> component (.getByRole "dialog") (.querySelector "form")))
          (f component))))))
