(ns tests.user
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component
                                    test-app
                                    change
                                    submit)]
            [ui.utils.i18n :refer (tr)]))

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

(defn with-submit-register [ctx f]
  (let [{:keys [mocks]} ctx
        [submit-res] mocks
        {:keys [email password organization]} (some-> submit-res :request :variables)]

    (with-mounted-component
      [test-app {:route "/register" :mocks mocks}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component (tr [:field/email])) email)
          (change (.getByLabelText component (tr [:field/password])) password)
          (change (.getByLabelText component (tr [:field/organization])) organization)
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))

(defn with-submit-login [ctx f]
  (let [{:keys [mocks]} ctx
        [submit-res] mocks
        {:keys [email password]} (some-> submit-res :request :variables)]

    (with-mounted-component
      [test-app {:route "/login" :mocks mocks}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component (tr [:field/email])) email)
          (change (.getByLabelText component (tr [:field/password])) password)
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
