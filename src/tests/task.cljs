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

(defn create-task [variables]
  (p/let [query (inline "mutations/task/create-task.graphql")
          request  {:query query :variables variables}
          result (send request)]
    {:request request
     :result result}))

(defn fetch-organization-task-options []
  (p/let [query (inline "queries/user/organization/fetch-task-options.graphql")
          request  {:query query}
          result (send request)]
    {:request request
     :result result}))

(defn fetch-organization-tasks
  ([]
   (p/let [query (inline "queries/user/organization/fetch-tasks.graphql")
           request  {:query query}
           result (send request)]
     {:request request
      :result result}))
  ([variables]
   (p/let [query (inline "queries/user/organization/fetch-tasks.graphql")
           request  {:query query :variables variables}
           result (send request)]
     {:request request
      :result result})))

(defn with-submit [ctx f]
  (let [{:keys [mocks]} ctx
        [fetch-mock create-mock] mocks
        {:keys [agents places]} (-> fetch-mock :result :data :user :organization)
        {:keys [agentId startAt placeIds route]} (-> create-mock :request :variables)
        agent (some #(when (= (:id %) agentId) %) agents)
        {:keys [legs path]} route
        sorted-places (sort-by-id places placeIds)]

    (with-mounted-component
      [test-app
       {:route "/organization/tasks/create"
        :mocks mocks}]
      (fn [^js component user]
        (p/do
          (.findByText component (tr [:field/submit]))
          (select-combobox user component (tr [:field/agent]) (-> agent :name))

          (change
           (.getByLabelText component (tr [:field/departure]))
           (to-datetime-local (js/Date. startAt)))

          (set! js/google (mock-google
                           {:routes
                            [{:legs (mapv (fn [{:keys [distance duration address location]}]
                                            {:distance {:value distance}
                                             :duration {:value duration}
                                             :end_address address
                                             :end_location location})
                                          legs)
                              :overview_path (mapv (fn [{:keys [lat lng]}]
                                                     {:lat (fn [] lat)
                                                      :lng (fn [] lng)})
                                                   path)
                              :bounds {:getNorthEast mock-lat-lng
                                       :getSouthWest mock-lat-lng}}]}))
          (init-directions)

          #_{:clj-kondo/ignore [:unresolved-symbol]}
          (p/doseq [{place-name :name} sorted-places]
            (select-combobox user component (tr [:field/add-stop]) place-name)
            (.findByText component place-name))

          (.findByText component (str (tr [:misc/loading]) "..."))
          (.findByText component (tr [:field/submit]))

          (submit (-> component (.-container) (.querySelector "form")))
          (f component))))))
