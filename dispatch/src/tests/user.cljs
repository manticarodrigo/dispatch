(ns tests.user
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component
                                    test-app
                                    change
                                    submit)]
            [ui.utils.i18n :refer (tr)]))

(defn register [variables]
  (p/let [query (inline "mutations/user/register.graphql")
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

(defn login-confirm [variables]
  (p/let [query (inline "mutations/user/login-confirm.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:query query
     :variables variables
     :request request
     :result result}))

(defn scope []
  (p/let [query (inline "queries/user/fetch-scope.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))

(defn with-submit-register [ctx f]
  (p/let [{:keys [mocks]} ctx
          [submit-res] mocks
          {:keys [request]} submit-res
          {:keys [email organization]} (some-> request :variables)]

    (with-mounted-component
      [test-app {:route "/register" :mocks mocks}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component (tr [:field/email])) email)
          (change (.getByLabelText component (tr [:field/organization])) organization)
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))

(defn with-submit-login [ctx f]
  (p/let [{:keys [mocks]} ctx
          [submit-res] mocks
          {:keys [request]} submit-res
          {:keys [email]} (some-> request :variables)]

    (with-mounted-component
      [test-app {:route "/login" :mocks mocks}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component (tr [:field/email])) email)
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))

(defn with-submit-confirm [ctx f]
  (p/let [{:keys [mocks]} ctx
          [submit-res] mocks
          {:keys [request]} submit-res
          {:keys [code]} (some-> request :variables)]

    (with-mounted-component
      [test-app {:route "/login/confirm" :mocks mocks}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component (tr [:field/code])) code)
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))

(defn with-scope-loader [_ f]
  (p/let [scope-mock (scope)]
    (with-mounted-component
      [test-app {:route "/" :mocks [scope-mock]}]
      (fn [^js component]
        (f component)))))
