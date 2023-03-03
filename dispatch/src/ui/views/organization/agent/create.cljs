(ns ui.views.organization.agent.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.title :refer (title)]
            [ui.components.forms.agent :refer (agent-form)]))

(defn view []
  [map-layout
   [title {:title (tr [:view.agent.create/title])}]
   [agent-form]])
