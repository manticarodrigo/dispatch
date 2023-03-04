(ns ui.views.organization.plan.detail
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-mutation)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]))

(def OPTIMIZE_PLAN (gql (inline "mutations/plan/optimize.graphql")))

(defn view []
  (let [{plan-id :plan} (use-params)
        [optimize status] (use-mutation OPTIMIZE_PLAN {})]
    [map-layout
     [header {:title (tr [:view.plan.create/title])}]
     [:button {:on-click #(optimize {:variables {:planId plan-id}})} "Optimize"]]))
