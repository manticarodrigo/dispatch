(ns ui.views.organization.agent.create
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]
            [ui.components.forms.agent :refer (agent-form)]))

(defn view []
  [map-layout
   [header {:title (tr [:view.agent.create/title])}]
   [:div {:class "p-4 overflow-y-auto"}
    [agent-form]]])
