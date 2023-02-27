(ns ui.components.forms.login
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def LOGIN_USER (gql (inline "mutations/user/login.graphql")))

(defn login-form []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [{:keys [email phone]} @!state
            [login status] (use-mutation LOGIN_USER {})
            {:keys [loading]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (login {:variables @!state})
                      (.then #(navigate "/login/confirm"))
                      (.catch #(reset! !anoms (parse-anoms %)))))}
         (when (empty? phone)
           [input {:type "email"
                   :label (tr [:field/email])
                   :value email
                   :class "mb-4"
                   :on-text #(swap! !state assoc :email %)}])
         (when (empty? email)
           [input {:type "phone"
                   :label (tr [:field/phone])
                   :value phone
                   :class "mb-4"
                   :on-text #(swap! !state assoc :phone %)}])
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
