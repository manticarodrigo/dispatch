(ns ui.components.forms.login-phone
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def LOGIN_PHONE (gql (inline "mutations/user/login-phone.graphql")))

(defn login-phone-form []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [{:keys [phone]} @!state
            [login status] (use-mutation LOGIN_PHONE {})
            {:keys [loading]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (login {:variables @!state})
                      (.then #(navigate "/confirm"))
                      (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:type "phone"
                 :label (tr [:field/phone])
                 :value phone
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :phone %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
