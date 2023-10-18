(ns ui.components.forms.login-confirm
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.session :refer (create-session)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def LOGIN_CONFIRM (gql (inline "mutations/user/login-confirm.graphql")))

(defn login-confirm-form []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [{:keys [code]} @!state
            [login status] (use-mutation LOGIN_CONFIRM {})
            {:keys [loading ^js client]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (login {:variables {:code (-> @!state :code js/parseInt)}})
                      (.then (fn [res]
                               (create-session (-> res ->clj :data :loginConfirm))
                               (.resetStore client)
                               (navigate "/")))
                      (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:label (tr [:field/code])
                 :value code
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :code %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
