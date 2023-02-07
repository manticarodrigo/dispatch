(ns tests.seat
  (:require ["@faker-js/faker" :refer (faker)]
            [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component test-app change submit)]))

(defn create []
  (p/let [query (inline "mutations/seat/create.graphql")
          variables {:name (.. faker -name fullName)}
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn find-all []
  (p/let [query (inline "queries/seat/fetch-all.graphql")
          request  {:query query}
          result (send request)]
    {:request request
     :result result}))

(defn find-unique [variables]
  (p/let [query (inline "queries/seat/fetch.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn with-submit [ctx f]
  (let [{:keys [mocks]} ctx
        [create-mock] mocks
        {:keys [request]} create-mock
        {:keys [variables]} request]

    (with-mounted-component
      [test-app {:route "/admin/seats/create" :mocks mocks}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component "Name") (:name variables))
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
