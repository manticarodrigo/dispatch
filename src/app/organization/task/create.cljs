(ns app.organization.task.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.forms.task :refer (task-form)]))

(defn view []
  [map-layout {:title (tr [:view.task.create/title])}
   [task-form]])
