(ns ui.views.login
  (:require [ui.lib.router :refer (link)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.forms.login :refer (login-form)]))

(defn view []
  [:div {:class "flex flex-col justify-center items-center w-full h-full"}
   [:h1 {:class "mb-2 text-lg"}
    (tr [:view.login/title])]
   [login-form]
   [:div {:class "pt-2 text-center"}
    [:p {:class "text-sm"} (tr [:view.login.register-link/title]) " "
     [link {:to "/register" :class "underline"} (tr [:view.login.register-link/link])]]]])
