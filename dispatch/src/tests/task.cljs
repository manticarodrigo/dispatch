(ns tests.task
  (:require [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [common.utils.date :refer (to-datetime-local)]
            [ui.lib.google.maps.directions :refer (init-directions)]
            [ui.utils.i18n :refer (tr)]
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

(defn create [variables]
  (p/let [query (inline "mutations/task/create.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn find-all
  ([]
   (p/let [query (inline "queries/task/fetch-all.graphql")
           request  {:query query}
           result (send request)]
     {:request request
      :result result}))
  ([variables]
   (p/let [query (inline "queries/task/fetch-all.graphql")
           request  {:query query :variables variables}
           result (send request)]
     {:request request
      :result result})))

(defn with-submit [ctx f]
  (let [{:keys [mocks]} ctx
        [fetch-mock create-mock] mocks
        {:keys [seats places]} (-> fetch-mock :result :data :user)
        {:keys [seatId startAt placeIds route]} (-> create-mock :request :variables)
        seat (some #(when (= (:id %) seatId) %) seats)
        {:keys [path]} route
        overview-path (mapv (fn [{:keys [lat lng]}]
                              #js{:lat (fn [] lat)
                                  :lng (fn [] lng)})
                            path)
        sorted-places (sort-by-id places placeIds)
        origin-place (first sorted-places)
        destination-place (last sorted-places)
        stops (->> sorted-places (drop 1) (drop-last 1))]

    (with-mounted-component
      [test-app
       {:route "/admin/tasks/create"
        :mocks mocks}]
      (fn [^js component user]
        (p/do
          (.findByText component (tr [:field/submit]))
          (select-combobox user component (tr [:field/seat]) (-> seat :name))

          (change
           (.getByLabelText component (tr [:field/departure]))
           (to-datetime-local (js/Date. startAt)))

          (select-combobox user component (tr [:field/origin]) (-> origin-place :name))
          (select-combobox user component (tr [:field/destination]) (-> destination-place :name))

          (set! js/google (mock-google
                           {:routes
                            [{:legs []
                              :overview_path overview-path
                              :bounds {:getNorthEast mock-lat-lng
                                       :getSouthWest mock-lat-lng}}]}))
          (init-directions)

          #_{:clj-kondo/ignore [:unresolved-symbol]}
          (p/doseq [{place-name :name} stops]
            (select-combobox user component (tr [:field/add-stop]) place-name)
            (.findByText component place-name))

          (.findByText component (str (tr [:generic/loading]) "..."))
          (.findByText component (tr [:field/submit]))

          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
