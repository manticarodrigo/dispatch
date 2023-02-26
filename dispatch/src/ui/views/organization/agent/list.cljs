(ns ui.views.organization.agent.list
  (:require [react]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.string :refer (filter-text)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.filters :refer (filters)]
            [ui.components.lists.agent :refer (agent-list)]))

(def FETCH_ORGANIZATION_AGENTS (gql (inline "queries/user/organization/fetch-agents.graphql")))

(defn view []
  (let [[{:keys [text] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query FETCH_ORGANIZATION_AGENTS {})
        agents (some-> data :user :organization :agents)
        filtered-agents (filter-text text :name agents)]

    (react/useEffect
     (fn []
       (dispatch [:map
                  {:locations
                   (mapv
                    (fn [{:keys [name location]}]
                      {:title name
                       :position (:position location)})
                    (filter #(:location %) filtered-agents))}])
       #())
     #js[agents text])

    [:div {:class padding}
     [title {:title (tr [:view.agent.list/title])
             :create-link "create"}]
     [filters {:search text
               :on-search-change #(set-search-params
                                   (if (empty? %)
                                     (dissoc search-params :text)
                                     (assoc search-params :text %)))}]
     [agent-list {:agents filtered-agents :loading loading}]]))
