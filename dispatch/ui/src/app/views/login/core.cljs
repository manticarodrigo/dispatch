(ns app.views.login.core
  (:require
   ["@apollo/client" :refer (gql useMutation)]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj ->js)]
   [app.utils.cookie :refer (create-session)]
   [app.utils.i18n :refer (tr)]
   [app.utils.error :refer (tr-error)]
   [app.components.generic.input :refer (input)]
   [app.components.generic.button :refer (button)]))

(def LOGIN
  (gql
   "
    mutation Login($email: String!, $password: String!) {
      login(email: $email, password: $password)
    }
  "))

(defn login-view []
  (let [!s (r/atom {:email "" :password ""})
        !e (r/atom nil)]
    (fn []
      (let [[login] (useMutation LOGIN)]
        (prn @!e)
        (js/console.log @!e)
        [:div {:class "flex justify-center items-center w-full lg:h-screen overflow-y-auto lg:overflow-hidden"}
         [:div {:class "py-6 px-3"}
          [:h1 {:class "mb-6 text-2xl text-white"} (tr [:view.login/title])]
          [:form {:class "flex flex-col"
                  :on-submit
                  (fn [e]
                    (.preventDefault e)
                    (-> (login (->js {:variables @!s}))
                        (.then #(create-session (-> % ->clj :data :login)))
                        (.catch #(reset! !e (-> %
                                                .-graphQLErrors
                                                ->clj
                                                first
                                                :extensions
                                                :anomaly)))))}
           [input {:id "email"
                   :type "email"
                   :label (tr [:view.login.fields/email])
                   :value (:email @!s)
                   :class "pb-4"
                   :on-text #(swap! !s assoc :email %)}]
           [input {:id "password"
                   :type "password"
                   :label (tr [:view.login.fields/password])
                   :value (:password @!s)
                   :class "pb-4"
                   :on-text #(swap! !s assoc :password %)}]
           [button {:label (tr [:view.login.fields/submit]) :class "my-4"}]
           (when @!e [:span {:class "p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                      (tr-error @!e)])]]]))))
