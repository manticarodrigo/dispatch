(ns ui.views.admin.task.create
  (:require [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.components.forms.task :refer (task-form)]))

(defn view []
  [:div {:class (class-names padding)}
   [task-form]])
