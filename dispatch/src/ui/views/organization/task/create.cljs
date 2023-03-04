(ns ui.views.organization.task.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]
            [ui.components.forms.task :refer (task-form)]))

(defn view []
  [map-layout
   [header {:title (tr [:view.task.create/title])}]
   [:div {:class "p-4 overflow-y-auto"}
    [task-form]]])
