(ns ui.views.agent.place.list
  (:require [react]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params use-search-params)]
            [ui.utils.string :refer (filter-text)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.filters :refer (filters)]
            [ui.components.lists.place :refer (place-list)]))

(def FETCH_PLACES (gql (inline "queries/place/fetch-all-by-device.graphql")))

(defn view []
  (let [{agent-id :agent} (use-params)
        device (listen [:device])
        device-id (:id device)
        [{:keys [text] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query FETCH_PLACES {:variables
                                                        {:agentId agent-id
                                                         :deviceId device-id}})
        places (some-> data :places)
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

    [:div {:class padding}
     [title {:title (tr [:view.place.list/title])
             :create-link "create"}]
     [filters {:search text
               :on-search-change #(set-search-params
                                   (if (empty? %)
                                     (dissoc search-params :text)
                                     (assoc search-params :text %)))}]
     [place-list {:places filtered-places :loading loading}]]))