(ns ui.views.admin.task.create
  (:require [ui.utils.css :refer (padding)]
            [ui.components.forms.task :refer (task-form)]))

(defn view []
  [:div {:class padding}
   [task-form]])
