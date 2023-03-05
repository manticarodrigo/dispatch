(ns ui.views.organization.place.list
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.string :refer (filter-text)]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.layout.header :refer (header)]
            [ui.components.filters :refer (filters)]
            [ui.components.lists.place :refer (place-list)]))

(def FETCH_ORGANIZATION_PLACES (gql (inline "queries/user/organization/fetch-places.graphql")))

(defn view []
  (let [[{:keys [text] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query FETCH_ORGANIZATION_PLACES {})
        places (some-> data :user :organization :places)
        filtered-places (filter-text text :name places)]

    (use-map-items
     loading
     {:places filtered-places}
     [places text])

    [map-layout
     [header {:title (tr [:view.place.list/title])
              :create-link "create"}]
     [filters {:search text
               :on-search-change #(set-search-params
                                   (if (empty? %)
                                     (dissoc search-params :text)
                                     (assoc search-params :text %)))}]
     [place-list {:places filtered-places :loading loading}]]))
