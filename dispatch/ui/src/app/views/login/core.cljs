(ns app.views.login.core
  (:require
   ["axios$default" :as axios]
   [reagent.core :as r]
   [cljs-bean.core :refer (->js)]
   [app.config :as config]
   [app.utils.cookie :refer (create-session)]
   [app.components.generic.input :refer (input)]
   [app.components.generic.button :refer (button)]))

(defn login-view []
  (let [s (r/atom {:email "" :password ""})]
    (fn []
      [:div {:class "flex justify-center items-center w-full lg:h-screen overflow-y-auto lg:overflow-hidden"}
       [:div {:class "py-6 px-3"}
        [:h1 {:class "mb-6 text-2xl text-white"} "Login"]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (.post axios (str config/API_URL "/login") (->js @s))
                      (.then #(create-session (-> % .-data .-sessionId)))
                      (.catch #(prn %))))}
         [input {:id "email"
                 :type "email"
                 :label "Email"
                 :value (:email @s)
                 :class "pb-4"
                 :on-text #(swap! s assoc :email %)}]
         [input {:id "password"
                 :type "password"
                 :label "Password"
                 :value (:password @s)
                 :class "pb-4"
                 :on-text #(swap! s assoc :password %)}]
         [button {:label "Submit" :class "my-4"}]]]])))
