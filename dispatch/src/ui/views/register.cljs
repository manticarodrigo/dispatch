(ns ui.views.register
  (:require [ui.lib.router :refer (link)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.register :refer (register-form)]))

(defn view []
  [:div {:class padding}
   [title {:title (tr [:view.register/title])}]
   [register-form]
   [:div {:class "pt-2 text-center"}
    [:p (tr [:view.register.login/title]) " "
     [link {:to "/login" :class "underline"} (tr [:view.register.login/link])]]]])
