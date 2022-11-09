(ns ui.components.forms.user
  (:require [reagent.core :as r]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.error :refer (tr-error)]
            [ui.components.inputs.generic.input :refer (input)]
            [ui.components.inputs.generic.button :refer (button)]))

(defn user-form [{initial-state :initial-state on-submit :on-submit}]
  (let [!state (r/atom (or initial-state {}))
        !anoms (r/atom {})]
    (fn []
      [:form {:class "flex flex-col"
              :on-submit on-submit}
       [input {:id "username"
               :label (tr [:view.register.fields/username])
               :value (:username @!state)
               :required true
               :class "pb-4"
               :on-text #(swap! !state assoc :username %)}]
       [input {:id "firstName"
               :label (tr [:view.register.fields/firstName])
               :value (:firstName @!state)
               :required true
               :class "pb-4"
               :on-text #(swap! !state assoc :firstName %)}]
       [input {:id "lastName"
               :label (tr [:view.register.fields/lastName])
               :value (:lastName @!state)
               :required true
               :class "pb-4"
               :on-text #(swap! !state assoc :lastName %)}]
       [input {:id "email"
               :type "email"
               :label (tr [:view.register.fields/email])
               :value (:email @!state)
               :required true
               :class "pb-4"
               :on-text #(swap! !state assoc :email %)}]
       [button {:label "Save" :class "my-4"}]
       (doall (for [anom @!anoms]
                [:span {:key (:reason anom)
                        :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                 (tr-error anom)]))])))
