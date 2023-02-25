(ns ui.views.confirm
  (:require [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.login-confirm :refer (login-confirm-form)]))

(defn view []
  [:div {:class padding}
   [title {:title (tr [:view.confirm/title])}]
   [login-confirm-form]])
