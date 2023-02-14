(ns ui.views.admin.agent.create
  (:require [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.agent :refer (agent-form)]))

(defn view []
  [:div {:class padding}
   [title {:title (tr [:view.agent.create/title])}]
   [agent-form]])
