(ns ui.views.register
  (:require [ui.lib.router :refer (link)]
            [ui.utils.css :refer (padding)]
            [ui.components.forms.register :refer (register-form)]))

(defn view []
  [:div {:class "w-full h-full overflow-y-auto"}
   [:div {:class padding}
    [register-form]
    [:div {:class "pt-2 text-center"}
     [:p "Already have an account? "
      [link {:to "/login" :class "underline"} "Login here."]]]]])
