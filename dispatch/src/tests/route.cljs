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

(defn sort-by-id [coll order]
  (let [id-map (zipmap (map :id coll) coll)]
    (map #(id-map %) order)))

(defn fetch-routes
  ([]
   (p/let [query (inline "queries/route/fetch-all.graphql")
           request  {:query query}
           result (send request)]
     {:request request
      :result result}))
  ([variables]
   (p/let [query (inline "queries/route/fetch-all.graphql")
           request  {:query query :variables variables}
           result (send request)]
     {:request request
      :result result})))

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
        {:keys [seatId startAt addressIds route]} (-> create-mock :request :variables)
        seat (some #(when (= (:id %) seatId) %) seats)
        {:keys [path]} route
        overview-path (mapv (fn [{:keys [lat lng]}]
                              #js{:lat (fn [] lat)
                                  :lng (fn [] lng)})
                            path)
        sorted-addresses (sort-by-id addresses addressIds)
        origin-address (first sorted-addresses)
        destination-address (last sorted-addresses)
        waypoints (->> sorted-addresses (drop 1) (drop-last 1))]

    (with-mounted-component
      [test-app
       {:route "/admin/routes/create"
        :mocks mocks}]
      (fn [^js component user]
        (p/do
          (.findByText component "Create route")
          (select-combobox user component "Assigned seat" (-> seat :name))

          (change
           (.getByLabelText component "Departure time")
           (to-datetime-local (js/Date. startAt)))

          (select-combobox user component "Origin address" (-> origin-address :name))
          (select-combobox user component "Destination address" (-> destination-address :name))

          (set! js/google (mock-google
                           {:routes
                            [{:legs []
                              :overview_path overview-path
                              :bounds {:getNorthEast mock-lat-lng
                                       :getSouthWest mock-lat-lng}}]}))
          (init-directions)

          #_{:clj-kondo/ignore [:unresolved-symbol]}
          (p/doseq [{address-name :name} waypoints]
            (select-combobox user component "Add new address" address-name)
            (.findByText component address-name))

          (.findByText component "Loading...")
          (.findByText component "Create route" #js{} #js{:timeout 3000})

          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
