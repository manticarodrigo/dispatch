(ns app.views.login.core
  (:require
   ["axios$default" :as axios]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj ->js)]
   [app.config :as config]
   [app.utils.cookie :refer (create-session)]
   [app.utils.i18n :refer (tr)]
   [app.utils.error :refer (tr-error)]
   [app.components.generic.input :refer (input)]
   [app.components.generic.button :refer (button)]))

(defn login-view []
  (let [!s (r/atom {:email "" :password ""})
        !e (r/atom nil)]
    (fn []
      (prn @!e)
      [:div {:class "flex justify-center items-center w-full lg:h-screen overflow-y-auto lg:overflow-hidden"}
       [:div {:class "py-6 px-3"}
        [:h1 {:class "mb-6 text-2xl text-white"} (tr [:view.login/title])]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (.post axios (str config/API_URL "/login") (->js @!s))
                      (.then #(create-session (.. % -data -sessionId)))
                      (.catch #(reset! !e (->clj (.. % -response -data))))))}
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
                    (tr-error @!e)])]]])))
