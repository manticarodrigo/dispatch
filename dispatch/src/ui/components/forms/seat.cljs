(ns ui.components.forms.seat
  (:require ["@apollo/client" :refer (gql useMutation)]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->js)]
            [ui.lib.apollo :refer (parse-anoms)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.error :refer (tr-error)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.button :refer (button)]))

(def FETCH_SEATS (gql (inline "queries/seat/fetch-all.graphql")))
(def CREATE_SEAT (gql (inline "mutations/seat/create.graphql")))

(defn seat-form [{on-close :on-close}]
  (let [!state (r/atom {})
        !anoms (r/atom {})]
    (fn []
      (let [[create] (useMutation
                      CREATE_SEAT
                      (->js {:refetchQueries [{:query FETCH_SEATS}]}))
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit (fn [e]
                             (.preventDefault e)
                             (-> (create (->js {:variables @!state}))
                                 (.then (fn []
                                          (on-close)
                                          (navigate "/fleet")))
                                 (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:id "name"
                 :label "Name"
                 :value (:name @!state)
                 :required true
                 :class "pb-4"
                 :on-text #(swap! !state assoc :name %)}]
         [button {:label "Submit" :class "my-4"}]
         (doall (for [anom @!anoms]
                  [:span {:key (:reason anom)
                          :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                   (tr-error anom)]))]))))
