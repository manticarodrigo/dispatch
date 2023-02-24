(ns tests.agent
  (:require ["@faker-js/faker" :refer (faker)]
            [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component test-app change submit)]
            [ui.utils.i18n :refer (tr)]))

(defn create []
  (p/let [query (inline "mutations/agent/create.graphql")
          variables {:name (.. faker -name fullName)
                     :phone (.. faker -phone (number "+505########"))}
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn find-all []
  (p/let [query (inline "queries/agent/fetch-all.graphql")
          request  {:query query}
          result (send request)]
    {:request request
     :result result}))

(defn find-unique [variables]
  (p/let [query (inline "queries/agent/fetch.graphql")
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
      [test-app {:route "/admin/agents/create" :mocks mocks}]
      (fn [^js component]
        (p/do
          (change (.getByLabelText component (tr [:field/name])) (:name variables))
          (change (.getByLabelText component (tr [:field/phone])) (:phone variables))
          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
