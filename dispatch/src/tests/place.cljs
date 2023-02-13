(ns tests.place
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component
                                    test-app
                                    change
                                    select
                                    get-combobox
                                    submit)]
            [ui.utils.i18n :refer (tr)]
            [ui.lib.google.maps.autocomplete :refer (init-autocomplete)]
            [ui.lib.google.maps.places :refer (init-places)]
            [tests.mocks.google :refer (mock-google)]))

(defn create [variables]
  (p/let [query (inline "mutations/place/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn find-all []
  (p/let [query (inline "queries/place/fetch-all.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))

(defn with-submit [ctx f]
  (let [{:keys [mocks]} ctx
        {:keys [name description lat lng]} (-> mocks first :request :variables)]

    (with-mounted-component
      [test-app {:route "/admin/places/create" :mocks mocks}]
      (fn [^js component user]
        (p/do
          (change (.getByLabelText component (tr [:field/name])) name)

          (set! js/google (mock-google [{:place_id "1" :description description}]))
          (init-autocomplete)

          (change (get-combobox component (tr [:field/location])) description)

          (.findByText component description)

          (set! js/google (mock-google {:geometry
                                        {:location
                                         {:lat (fn [] lat)
                                          :lng (fn [] lng)}}}))
          (init-places nil)

          (select user (.getByRole component "listbox") description)

          (.findByText component "found coordinates")

          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
