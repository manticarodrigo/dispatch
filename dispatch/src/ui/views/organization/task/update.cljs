(ns ui.views.organization.task.update
  (:require [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.forms.task :refer (task-form)]))

(defn view []
  (let [{task-id :task} (use-params)]
    [map-layout {:title (tr [:view.task.update/title])}
     [:div {:class "p-4 overflow-y-auto"}
      [task-form {:id task-id}]]]))
