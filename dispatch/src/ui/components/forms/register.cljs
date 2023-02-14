(ns ui.components.forms.register
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

(def CREATE_USER (gql (inline "mutations/user/create.graphql")))

(defn register-form []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [{:keys [email password]} @!state
            [register status] (use-mutation CREATE_USER {})
            {:keys [loading ^js client]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (register {:variables @!state})
                      (.then (fn [res]
                               (create-session (-> res ->clj :data :createUser))
                               (.resetStore client)
                               (navigate "/admin/tasks")))
                      (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:id "email"
                 :type "email"
                 :label (tr [:field/email])
                 :value email
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :email %)}]
         [input {:id "password"
                 :type "password"
                 :label (tr [:field/password])
                 :value password
                 :required true
                 :class "mb-4"
                 :on-text #(swap! !state assoc :password %)}]
         [submit-button {:loading loading}]
         [errors @!anoms]]))))
