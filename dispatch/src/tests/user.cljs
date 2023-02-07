(ns tests.user
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component
                                    test-app
                                    change
                                    submit)]))

(defn active-user []
  (p/let [query (inline "queries/user/fetch.graphql")
          request  {:query query}
          result (send request)]
    {:request request
     :result result}))

(defn register [variables]
  (p/let [query (inline "mutations/user/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:query query
     :variables variables
     :request request
     :result result}))

(defn login [variables]
  (p/let [query (inline "mutations/user/login.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:query query
     :variables variables
     :request request
     :result result}))

(defn with-submit [ctx f]
  (let [{:keys [route mocks]} ctx
        [submit-res] mocks
        {:keys [email password]} (some-> submit-res :request :variables)]

    (with-mounted-component
      [test-app {:route route :mocks mocks}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component "Email") email)
          (change (.getByLabelText component "Password") password)
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))

(defn with-submit-register [ctx f]
  (with-submit (assoc ctx :route "/register") f))

(defn with-submit-login [ctx f]
  (with-submit (assoc ctx :route "/login") f))
