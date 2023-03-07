(ns ui.views.organization.task.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.forms.task :refer (task-form)]))

(defn view []
  [map-layout {:title (tr [:view.task.create/title])}
   [:div {:class "p-4 overflow-y-auto"}
    [task-form]]])
