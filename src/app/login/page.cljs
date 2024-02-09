(ns app.login.page
  (:require [reagent.core :as r]
            [ui.lib.router :refer (link)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.forms.login :refer (login-form)]))

(r/set-default-compiler! (r/create-compiler {:function-components true}))

(defn view []
  [:div {:class "flex flex-col justify-center items-center w-full h-full"}
   [:h1 {:class "mb-2 text-lg"}
    (tr [:view.login/title])]
   [login-form]
   [:div {:class "pt-2 text-center"}
    [:p {:class "text-sm"} (tr [:view.login.register-link/title]) " "
     [link {:to "/register" :class "underline"} (tr [:view.login.register-link/link])]]]])

(def ^:export LoginView (r/reactify-component view))
