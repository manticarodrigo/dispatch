(ns ui.views.stop.detail
  (:require [date-fns :as d]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->js)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql use-query use-mutation parse-anoms)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.utils.error :refer (tr-error)]
            [ui.components.inputs.generic.button :refer (button)]
            [ui.components.inputs.generic.input :refer (input)]))

(def FETCH_STOP (gql (inline "queries/stop/fetch.graphql")))
(def CREATE_STOP_ARRIVAL (gql (inline "mutations/stop/create-stop-arrival.graphql")))

(defn view []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [params (use-params)
            query (use-query FETCH_STOP {:variables {:id (:id params)}})
            [create-stop-arrival] (use-mutation CREATE_STOP_ARRIVAL {})
            {:keys [data loading]} query
            {:keys [id address note arrivedAt]} (:stop data)
            {:keys [name description]} address]
        [:div {:class (class-names padding)}
         (when loading [:p "Loading..."])
         [:div
          [:div name]
          [:div description]
          (when arrivedAt
            [:div "Arrived at: " (-> arrivedAt (js/parseInt) (js/Date.) (d/format "hh:mmaaa"))])
          (when note
            [:div "Note: " note])]
         [:form {:class "my-4 flex flex-col"
                 :on-submit (fn [e]
                              (.preventDefault e)
                              (-> (create-stop-arrival (->js {:variables
                                                              {:stopId id
                                                               :note (:note @!state)}}))
                                  (.catch #(reset! !anoms (parse-anoms %)))))}
          [input {:label "Note"
                  :value (:note @!state)
                  :on-text #(swap! !state assoc :note %)}]
          [button {:label "Submit" :class "my-4"}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
