(ns app.organization.place.detail
  (:require [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params use-search-params)]
            [ui.utils.date :as d]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.filters :refer (filters)]
            [ui.components.lists.task :refer (task-list)]))

(def FETCH_ORGANIZATION_PLACE (gql (inline "queries/user/organization/fetch-place.graphql")))

(defn view []
  (let [{place-id :place} (use-params)
        [{:keys [date status] :as search-params} set-search-params] (use-search-params)
        query (use-query
               FETCH_ORGANIZATION_PLACE
               {:variables
                {:placeId place-id
                 :filters {:start (-> date parse-date d/startOfDay)
                           :end  (-> date parse-date d/endOfDay)
                           :status status}}})
        {:keys [data previousData loading]} query
        {:keys [place]} (some-> data :user :organization)
        {:keys [tasks]} place
        {:keys [name description]} (some-> (or data previousData) :user :organization :place)]

    (use-map-items
     loading
     {:places [place]}
     [place])

    [map-layout {:title name :subtitle description}
     [filters {:date (-> date parse-date d/startOfDay)
               :on-date-change #(set-search-params
                                 (assoc search-params :date (-> % .getTime)))
               :status (or status "ALL")
               :on-status-change #(set-search-params (if (= % "ALL")
                                                       (dissoc search-params :status)
                                                       (assoc search-params :status %)))}]
     [task-list {:tasks tasks :loading loading}]]))
