(ns tests.address
  (:require
   ["@faker-js/faker" :refer (faker)]
   [shadow.resource :refer (inline)]
   [promesa.core :as p]
   [tests.util.api :refer (send)]
   [tests.util.ui :refer (with-mounted-component
                           test-app
                           change
                           select
                           get-combobox
                           submit)]
   [ui.lib.google.maps.autocomplete :refer (init-autocomplete)]
   [ui.lib.google.maps.places :refer (init-places)]
   [tests.mocks.google :refer (mock-google)]))

(defn create-address []
  (p/let [query (inline "mutations/address/create.graphql")
          variables {:name (.. faker -company name)
                     :description (.. faker -address (streetAddress true))
                     :lat (js/parseFloat (.. faker -address latitude))
                     :lng (js/parseFloat (.. faker -address longitude))}
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn fetch-addresses []
  (p/let [query (inline "queries/address/fetch-all.graphql")
          request  {:query query}
          result (send request)]
    {:query query
     :request request
     :result result}))

(defn with-submit-address [ctx f]
  (let [{:keys [mocks]} ctx
        {:keys [name description lat lng]} (-> mocks first :request :variables)]

    (with-mounted-component
      [test-app {:route "/addresses/create" :mocks mocks}]
      (fn [^js component user]
        (p/do
          (change (.getByLabelText component "Name") name)

          (set! js/google (mock-google [{:place_id "1" :description description}]))
          (init-autocomplete)

          (change (get-combobox component "Location") description)

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
