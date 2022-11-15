(ns tests.route
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [common.utils.date :refer (to-datetime-local from-datetime-local)]
            [tests.util.api :refer (send)]
            [tests.util.ui :refer (with-mounted-component
                                    test-app
                                    change
                                    select-combobox
                                    submit)]))

(defn create-route [user-res]
  (p/let [query (inline "mutations/route/create.graphql")
          variables {:seatId (-> user-res :result :data :user :seats first :id)
                     :startAt (from-datetime-local (js/Date.))
                     :addressIds (mapv :id (-> user-res :result :data :user :addresses))}
          request  {:query query :variables variables}
          result (send request)]
    {:query query
     :variables variables
     :request request
     :result result}))

(defn with-submit-route [ctx f]
  (let [{:keys [mocks]} ctx
        [user-res route-res] mocks
        {:keys [seats addresses]} (-> user-res :result :data :user)]
    (with-mounted-component
      [test-app {:route "/route" :mocks mocks}]
      (fn [^js component user]
        (p/do
          (.findByText component "Loaded...")
          (select-combobox user component "Assigned seat" (-> seats first :name))

          (change
           (.getByLabelText component "Departure time")
           (to-datetime-local (js/Date. (-> route-res :request :variables :startAt))))

          #_{:clj-kondo/ignore [:unresolved-symbol]}
          (p/doseq [address addresses]
            (select-combobox user component "Add address" (-> address :name)))

          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
