(ns ui.components.forms.stop
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql use-mutation parse-anoms)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def CREATE_ARRIVAL (gql (inline "mutations/stop/create-arrival.graphql")))

(defn stop-form []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [{stop-id :stop} (use-params)
            [create-arrival status] (use-mutation CREATE_ARRIVAL {})
            {:keys [loading]} status
            {:keys [note]} @!state]
        [:form {:class "my-4 flex flex-col"
                :on-submit (fn [e]
                             (.preventDefault e)
                             (-> (create-arrival {:variables
                                                  {:stopId stop-id
                                                   :note (:note @!state)}})
                                 (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:id "note"
                 :label (tr [:field/note])
                 :value note
                 :on-text #(swap! !state assoc :note %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
