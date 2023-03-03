(ns ui.views.register
  (:require [ui.lib.router :refer (link)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.register :refer (register-form)]))

(defn view []
  [:div {:class "flex flex-col justify-center items-center w-full h-full"}
   [title {:title (tr [:view.register/title])}]
   [register-form]
   [:div {:class "pt-2 text-center"}
    [:p {:class "text-sm"} (tr [:view.register.login-link/title]) " "
     [link {:to "/login" :class "underline"} (tr [:view.register.login-link/link])]]]])
