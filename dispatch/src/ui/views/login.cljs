(ns ui.views.login
  (:require [ui.lib.router :refer (link)]
            [ui.utils.css :refer (padding)]
            [ui.components.forms.login :refer (login-form)]))

(defn view []
  [:div {:class "w-full h-full overflow-y-auto"}
   [:div {:class padding}
    [login-form]
    [:div {:class "pt-2 text-center"}
     [:p "Need an account? "
      [link {:to "/register" :class "underline"} "Register here."]]]]])
