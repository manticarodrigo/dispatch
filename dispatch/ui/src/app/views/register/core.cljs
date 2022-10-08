(ns app.views.register.core
  (:require
   ["axios$default" :as axios]
   [reagent.core :as r]
   [cljs-bean.core :refer (->js)]
   [app.config :as config]
   [app.components.generic.input :refer (input)]
   [app.components.generic.button :refer (button)]))

(defn register-view []
  (let [s (r/atom {:firstName ""
                   :lastName ""
                   :email ""
                   :password ""})]
    (fn []
      [:div {:class "flex justify-center items-center w-full lg:h-screen overflow-y-auto lg:overflow-hidden"}
       [:div {:class "py-6 px-3"}
        [:h1 {:class "mb-6 text-2xl text-white"} "Register"]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (.post axios (str config/API_URL "/register") (->js @s))
                      (.then #(prn %))
                      (.catch #(prn %))))}
         [input {:id "firstName"
                 :label "First Name"
                 :value (:firstName @s)
                 :required true
                 :class "pb-4"
                 :on-text #(swap! s assoc :firstName %)}]
         [input {:id "lastName"
                 :label "Last Name"
                 :value (:lastName @s)
                 :required true
                 :class "pb-4"
                 :on-text #(swap! s assoc :lastName %)}]
         [input {:id "email"
                 :type "email"
                 :label "Email"
                 :value (:email @s)
                 :required true
                 :class "pb-4"
                 :on-text #(swap! s assoc :email %)}]
         [input {:id "password"
                 :type "password"
                 :label "Password"
                 :value (:password @s)
                 :required true
                 :class "pb-4"
                 :on-text #(swap! s assoc :password %)}]
         [button {:label "Submit" :class "my-4"}]]]])))
