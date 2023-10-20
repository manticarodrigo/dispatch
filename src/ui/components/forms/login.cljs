(ns ui.components.forms.login
  (:require ["react" :refer (useState)]
            [shadow.resource :refer (inline)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.inputs.country-combobox :refer (countries)]
            [ui.components.errors :refer (errors)]))

(def LOGIN_USER (gql (inline "mutations/user/login.graphql")))

(defn login-form []
  (let [device (listen [:device])
        country-code (str "+"
                          (->> countries
                               (filter #(= (:value %) (:country device)))
                               first
                               :code))
        [state set-state] (useState {})
        [anoms set-anoms] (useState nil)
        {:keys [email phone]} state
        [login status] (use-mutation LOGIN_USER {})
        {:keys [loading]} status
        navigate (use-navigate)]
    [:form {:class "flex flex-col"
            :on-submit
            (fn [e]
              (.preventDefault e)
              (-> (login {:variables state})
                  (.then #(navigate "/login/confirm"))
                  (.catch #(set-anoms (parse-anoms %)))))}
     (when (empty? phone)
       [input {:type "email"
               :label (tr [:field/email])
               :value email
               :class "mb-4"
               :on-text #(set-state (assoc state :email %))}])
     (when (empty? email)
       [input {:type "phone"
               :label (tr [:field/phone])
               :placeholder country-code
               :value phone
               :class "mb-4"
               :on-text #(set-state (assoc state :phone (cond
                                                          (= "+" %) % ;; allow user to type + and then numbers 
                                                          (empty? phone) (str country-code %) ;; if phone is empty, add country code
                                                          :else %)))
               :on-validate #(or (empty? %)
                                 (and (empty? phone) (re-matches #"^[0-9]*$" %))
                                 (re-matches #"^\+[0-9]*$" %))}])
     [submit-button {:loading loading}]
     [errors anoms]]))
