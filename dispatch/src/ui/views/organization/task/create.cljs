(ns ui.views.organization.task.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.title :refer (title)]
            [ui.components.forms.task :refer (task-form)]))

(defn view []
  [map-layout
   [title {:title (tr [:view.task.create/title])}]
   [task-form]])
