(ns ui.views.login-confirm
  (:require [ui.lib.router :refer (link)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.login-confirm :refer (login-confirm-form)]))

(defn view []
  [:div {:class "flex flex-col justify-center items-center w-full h-full"}
   [title {:title (tr [:view.login-confirm/title])}]
   [login-confirm-form]
   [:div {:class "pt-2 text-center"}
    [:p {:class "text-sm"} (tr [:view.login-confirm.return-link/title]) " "
     [link {:to "/login" :class "underline"} (tr [:view.login-confirm.return-link/link])]]]])
