(ns app.organization.agents.create
  (:require [reagent.core :as r]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.forms.agent :refer (agent-form)]))

(defn view []
  [map-layout {:title (tr [:view.agent.create/title])}
   [:div {:class "p-4 overflow-y-auto"}
    [agent-form]]])

(def ^:export OrganizationAgentsCreateView (r/reactify-component (fn [] [:f> view])))
