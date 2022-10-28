(ns ui.views.login.core
  (:require
   ["@apollo/client" :refer (gql useMutation)]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj ->js)]
   [ui.lib.apollo :refer (parse-anoms)]
   [ui.lib.router :refer (use-navigate)]
   [ui.utils.cookie :refer (create-session)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.error :refer (tr-error)]
   [ui.components.generic.input :refer (input)]
   [ui.components.generic.button :refer (button)]))

(def LOGIN
  (gql
   "
    mutation Login($email: String!, $password: String!) {
      login(email: $email, password: $password)
    }
  "))

(defn login-view []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [[login] (useMutation LOGIN)
            navigate (use-navigate)]
        [:div {:class "flex justify-center items-center w-full h-full overflow-y-auto"}
         [:div {:class "py-6 px-3"}
          [:form {:class "flex flex-col"
                  :on-submit
                  (fn [e]
                    (.preventDefault e)
                    (-> (login (->js {:variables @!state}))
                        (.then (fn [res]
                                 (create-session (-> res ->clj :data :login))
                                 (navigate "/fleet")))
                        (.catch #(reset! !anoms (parse-anoms %)))))}
           [input {:id "email"
                   :type "email"
                   :label (tr [:view.login.fields/email])
                   :value (:email @!state)
                  ;;  :required true
                   :class "pb-4"
                   :on-text #(swap! !state assoc :email %)}]
           [input {:id "password"
                   :type "password"
                   :label (tr [:view.login.fields/password])
                   :value (:password @!state)
                  ;;  :required true
                   :class "pb-4"
                   :on-text #(swap! !state assoc :password %)}]
           [button {:label (tr [:view.login.fields/submit]) :class "my-4"}]
           (doall (for [anom @!anoms]
                    [:span {:key (:reason anom)
                            :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                     (tr-error anom)]))]]]))))
