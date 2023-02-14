(ns ui.views.login
  (:require [ui.lib.router :refer (link)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.login :refer (login-form)]))

(defn view []
  [:div {:class padding}
   [title {:title (tr [:view.login/title])}]
   [login-form]
   [:div {:class "pt-2 text-center"}
    [:p (tr [:view.login.register/title]) " "
     [link {:to "/register" :class "underline"} (tr [:view.login.register/link])]]]])
