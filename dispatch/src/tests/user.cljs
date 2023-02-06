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

(defn register-user [variables]
  (p/let [query (inline "mutations/user/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:query query
     :variables variables
     :request request
     :result result}))

(defn login-user [variables]
  (p/let [query (inline "mutations/user/login.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:query query
     :variables variables
     :request request
     :result result}))

(defn with-submit-user [ctx f]
  (let [{:keys [route mocks]} ctx
        [submit-res] mocks
        {:keys [request result]} submit-res
        {:keys [variables]} request]

    (with-mounted-component
      [test-app {:route route :mocks [{:request request :result result}]}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component "Email") (:email variables))
          (change (.getByLabelText component "Password") (:password variables))
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))

(defn with-submit-register [ctx f]
  (with-submit-user (assoc ctx :route "/register") f))

(defn with-submit-login [ctx f]
  (with-submit-user (assoc ctx :route "/login") f))
