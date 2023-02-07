(ns ui.components.forms.login
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.session :refer (create-session)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.string :refer (class-names)]
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.button :refer (button)]))

(def LOGIN_USER (gql (inline "mutations/user/login.graphql")))

(defn login-form []
  (let [!state (r/atom {})
        !anoms (r/atom nil)]
    (fn []
      (let [{:keys [email password]} @!state
            [login status] (use-mutation LOGIN_USER {})
            {:keys [loading ^js client]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (-> (login {:variables @!state})
                      (.then (fn [res]
                               (create-session (-> res ->clj :data :loginUser))
                               (.resetStore client)
                               (navigate "/admin/tasks")))
                      (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:id "email"
                 :type "email"
                 :label (tr [:fields/email])
                 :value email
                 :required true
                 :class "pb-4"
                 :on-text #(swap! !state assoc :email %)}]
         [input {:id "password"
                 :type "password"
                 :label (tr [:fields/password])
                 :value password
                 :required true
                 :class "pb-4"
                 :on-text #(swap! !state assoc :password %)}]
         [button
          {:label (if loading
                    [:span {:class "flex justify-center items-center"}
                     [spinner {:class "mr-2 w-5 h-5"}] "Loading..."]
                    "Submit")
           :class (class-names "my-4 w-full" (when loading "cursor-progress"))
           :disabled loading}]
         (doall (for [anom @!anoms]
                  [:span {:key (:reason anom)
                          :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                   (tr-error anom)]))]))))
