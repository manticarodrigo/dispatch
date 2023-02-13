(ns ui.components.forms.stop
  (:require [react]
            [date-fns :as d]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [re-frame.core :refer (dispatch)]
            [ui.lib.apollo :refer (gql use-query use-mutation parse-anoms)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.utils.error :refer (tr-error)]
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.input :refer (input)]))

(def FETCH_STOP (gql (inline "queries/stop/fetch.graphql")))
(def CREATE_ARRIVAL (gql (inline "mutations/stop/create-arrival.graphql")))

(defn stop-form []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [{stop-id :stop} (use-params)
            query (use-query FETCH_STOP {:variables {:stopId stop-id}})
            [create-arrival status] (use-mutation CREATE_ARRIVAL {})
            loading (or (:loading query) (:loading status))
            {:keys [id place note arrivedAt]} (-> query :data :stop)
            {:keys [name description lat lng]} place]

        (react/useEffect
         (fn []
           (dispatch [:map
                      {:points
                       (when place
                         [{:title name
                           :position {:lat lat
                                      :lng lng}}])}])
           #())
         #js[name lat lng])

        [:div {:class padding}
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
                              (-> (create-arrival {:variables
                                                   {:stopId id
                                                    :note (:note @!state)}})
                                  (.catch #(reset! !anoms (parse-anoms %)))))}
          [input {:label "Note"
                  :value (:note @!state)
                  :on-text #(swap! !state assoc :note %)}]
          [button
           {:label (if loading
                     [:span {:class "flex justify-center items-center"}
                      [spinner {:class "mr-2 w-5 h-5"}] "Loading..."]
                     "Submit")
            :class (class-names "my-4 w-full" (when loading "cursor-progress"))
            :disabled loading}]
          (doall (for [anom @!anoms]
                   [:span {:key (:reason anom)
                           :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                    (tr-error anom)]))]]))))
