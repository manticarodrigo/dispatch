(ns app.organization.plan.list
  (:require ["react" :refer (useState)]
            ["react-feather" :rename {Edit CreateIcon}]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.bare :refer (bare-layout)]
            [ui.components.lists.plan :refer (plan-list)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.forms.plan :refer (plan-form)]))

(def FETCH_ORGANIZATION_PLANS (gql (inline "queries/user/organization/fetch-plans.graphql")))

(defn view []
  (let [[state set-state] (useState {})
        {:keys [create-open?]} state
        {:keys [data loading]} (use-query FETCH_ORGANIZATION_PLANS {})
        {:keys [plans]} (some-> data :user :organization)]

    [bare-layout {:title (tr [:view.plan.list/title])
                  :actions [:div
                            [button {:label [:span {:class "flex items-center"}
                                             [:> CreateIcon {:class "mr-2 w-4 h-4"}]
                                             (tr [:verb/create])]
                                     :class "ml-2 capitalize"
                                     :on-click #(set-state (merge state {:create-open? true}))}]]}
     [modal
      {:show (or create-open? false)
       :title (tr [:view.plan.create/title])
       :on-close #(set-state (merge state {:create-open? false}))}
      [:div {:class "p-4 sm:w-96"}
       [plan-form {:on-submit #(set-state (merge state {:create-open? false}))}]]]
     [plan-list {:plans plans :loading loading}]]))
