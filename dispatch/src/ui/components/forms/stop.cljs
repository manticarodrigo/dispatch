(ns ui.components.forms.stop
  (:require ["react" :refer (useState)]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql use-mutation parse-anoms)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.inputs.radio-group :refer (radio-group)]
            [ui.components.errors :refer (errors)]))

(def CREATE_ARRIVAL (gql (inline "mutations/stop/create-arrival.graphql")))

(def !anoms (r/atom nil))

(def status-options
  [{:key "COMPLETE" :label "Complete"}
   {:key "INCOMPLETE" :label "Incomplete"}])

(defn stop-form [{:keys [id on-submit]}]
  (let [[state set-state] (useState {:status "COMPLETE"})
        params (use-params)
        stop-id (or id (:stop params))
        [create-arrival create-arrival-status] (use-mutation CREATE_ARRIVAL {})
        {:keys [loading]} create-arrival-status
        {:keys [note status]} state]
    [:form {:class "flex flex-col"
            :on-submit (fn [e]
                         (.preventDefault e)
                         (-> (create-arrival {:variables
                                              {:stopId stop-id
                                               :note (:note state)
                                               :status (:status state)}})
                             (.catch #(reset! !anoms (parse-anoms %)))
                             (.then #(when on-submit (on-submit)))))}
     [input {:label (tr [:field/note])
             :value note
             :class "mb-4"
             :on-text #(set-state (assoc state :note %))}]
     [radio-group
      {:sr-label "Status"
       :value status
       :options status-options
       :on-change #(set-state (assoc state :status %))}]
     [submit-button {:loading loading}]
     [errors @!anoms]]))
