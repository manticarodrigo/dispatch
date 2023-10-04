(ns ui.components.forms.register
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def REGISTER_USER (gql (inline "mutations/user/register.graphql")))

(defn register-form []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [{:keys [email organization]} @!state
            [register status] (use-mutation REGISTER_USER {})
            {:keys [loading]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (register {:variables @!state})
                      (.then #((navigate "/login/confirm")))
                      (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:type "email"
                 :label (tr [:field/email])
                 :value email
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :email %)}]
         [input {:label (tr [:field/organization])
                 :value organization
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :organization %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
