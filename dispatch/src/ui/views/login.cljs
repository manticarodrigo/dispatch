(ns ui.views.login
  (:require
   ["@apollo/client" :refer (gql useMutation)]
   [shadow.resource :refer (inline)]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj ->js)]
   [ui.lib.apollo :refer (parse-anoms)]
   [ui.lib.router :refer (use-navigate link)]
   [ui.utils.session :refer (create-session)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.error :refer (tr-error)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.input :refer (input)]
   [ui.components.inputs.generic.button :refer (button)]))

(def LOGIN_USER (gql (inline "mutations/user/login.graphql")))

(defn view []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [[login] (useMutation LOGIN_USER)
            navigate (use-navigate)]
        [:div {:class "w-full h-full overflow-y-auto"}
         [:div {:class padding}
          [:form {:class "flex flex-col"
                  :on-submit
                  (fn [e]
                    (.preventDefault e)
                    (-> (login (->js {:variables @!state}))
                        (.then (fn [res]
                                 (create-session (-> res ->clj :data :loginUser))
                                 (navigate "/fleet")))
                        (.catch #(reset! !anoms (parse-anoms %)))))}
           [input {:id "email"
                   :type "email"
                   :label (tr [:view.login.fields/email])
                   :value (:email @!state)
                   :required true
                   :class "pb-4"
                   :on-text #(swap! !state assoc :email %)}]
           [input {:id "password"
                   :type "password"
                   :label (tr [:view.login.fields/password])
                   :value (:password @!state)
                   :required true
                   :class "pb-4"
                   :on-text #(swap! !state assoc :password %)}]
           [button {:label (tr [:view.login.fields/submit]) :class "my-4"}]
           (doall (for [anom @!anoms]
                    [:span {:key (:reason anom)
                            :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                     (tr-error anom)]))]
          [:div {:class "pt-2 text-center"}
           [:p "Need an account? "
            [link {:to "/register" :class "underline"} "Register here."]]]]]))))
