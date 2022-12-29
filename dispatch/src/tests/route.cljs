(ns tests.route
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [common.utils.date :refer (to-datetime-local)]
            [ui.lib.google.maps.directions :refer (init-directions)]
            [tests.mocks.google :refer (mock-google mock-lat-lng)]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component
                                    test-app
                                    change
                                    select-combobox
                                    submit)]))

(defn fetch-routes []
  (p/let [query (inline "queries/route/fetch-all.graphql")
          request  {:query query}
          result (send request)]
    {:request request
     :result result}))

(defn create-route [variables]
  (p/let [query (inline "mutations/route/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn with-submit-route [ctx f]
  (let [{:keys [mocks]} ctx
        [fetch-mock create-mock] mocks
        {:keys [seats addresses]} (-> fetch-mock :result :data :user)
        {:keys [seatId startAt]} (-> create-mock :request :variables)
        seat (some #(when (= (:id %) seatId) %) seats)]
    (with-mounted-component
      [test-app {:route "/routes/create" :mocks mocks}]
      (fn [^js component user]
        (p/do
          (.findByText component "Loaded...")
          (select-combobox user component "Assigned seat" (-> seat :name))

          (change
           (.getByLabelText component "Departure time")
           (to-datetime-local (js/Date. startAt)))

          (set! js/google (mock-google
                           {:routes
                            [{:legs []
                              :overview_path []
                              :bounds {:getNorthEast mock-lat-lng
                                       :getSouthWest mock-lat-lng}}]}))
          (init-directions)

          #_{:clj-kondo/ignore [:unresolved-symbol]}
          (p/doseq [{:keys [name]} addresses]
            (select-combobox user component "Add address" name)
            (.findByText component name))

          (.findByText component "Loading route")
          (.findByText component "Loaded route" #js{} #js{:timeout 3000})

          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
