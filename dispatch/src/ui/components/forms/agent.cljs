(ns ui.components.forms.agent
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def CREATE_AGENT (gql (inline "mutations/agent/create.graphql")))

(defn agent-form []
  (let [!state (r/atom {})
        !anoms (r/atom {})]
    (fn []
      (let [{:keys [name]} @!state
            [create status] (use-mutation CREATE_AGENT {})
            {:keys [loading]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit (fn [e]
                             (.preventDefault e)
                             (-> (create {:variables @!state})
                                 (.then (fn []
                                          (navigate "/admin/agents")))
                                 (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:id "name"
                 :label (tr [:field/name])
                 :value name
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :name %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
