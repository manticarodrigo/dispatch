(ns ui.views.admin.task.create
  (:require [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.task :refer (task-form)]))

(defn view []
  [:div {:class padding}
   [title {:title (tr [:view.task.create/title])}]
   [task-form]])
