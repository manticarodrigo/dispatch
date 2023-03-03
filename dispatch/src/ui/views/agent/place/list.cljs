(ns ui.views.agent.place.list
  (:require [react]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.string :refer (filter-text)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.title :refer (title)]
            [ui.components.filters :refer (filters)]
            [ui.components.lists.place :refer (place-list)]))

(def FETCH_AGENT_PLACES (gql (inline "queries/user/agent/fetch-places.graphql")))

(defn view []
  (let [[{:keys [text] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query FETCH_AGENT_PLACES {})
        places (some-> data :user :agent :places)
        filtered-places (filter-text text :name places)]

    (react/useEffect
     (fn []
       (dispatch [:map
                  {:points
                   (mapv
                    (fn [{:keys [lat lng name]}]
                      {:title name
                       :position {:lat lat :lng lng}})
                    filtered-places)}])
       #())
     #js[places text])

    [map-layout
     [title {:title (tr [:view.place.list/title])
             :create-link "create"}]
     [filters {:search text
               :on-search-change #(set-search-params
                                   (if (empty? %)
                                     (dissoc search-params :text)
                                     (assoc search-params :text %)))}]
     [place-list {:places filtered-places :loading loading}]]))
