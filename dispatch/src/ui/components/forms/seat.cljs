(ns ui.components.forms.seat
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def CREATE_SEAT (gql (inline "mutations/seat/create.graphql")))

(defn seat-form []
  (let [!state (r/atom {})
        !anoms (r/atom {})]
    (fn []
      (let [{:keys [name]} @!state
            [create status] (use-mutation CREATE_SEAT {})
            {:keys [loading]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit (fn [e]
                             (.preventDefault e)
                             (-> (create {:variables @!state})
                                 (.then (fn []
                                          (navigate "/admin/seats")))
                                 (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:id "name"
                 :label (tr [:field/name])
                 :value name
                 :required true
                 :class "pb-4"
                 :on-text #(swap! !state assoc :name %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
